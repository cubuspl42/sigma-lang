package sigma.syntax.expressions

import sigma.BinaryOperationPrototype
import sigma.TypeScope
import sigma.SyntaxValueScope

import sigma.Thunk
import sigma.parser.antlr.SigmaParser.BinaryOperationAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionTupleLiteralAltContext
import sigma.syntax.SourceLocation
import sigma.semantics.types.FunctionType
import sigma.semantics.types.Type
import sigma.semantics.types.TypeVariableResolutionError
import sigma.evaluation.values.FunctionValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.TypeErrorException
import sigma.evaluation.scope.Scope

data class CallTerm(
    override val location: SourceLocation,
    // Idea: Rename to `callee`? (again?)
    val subject: ExpressionTerm,
    val argument: ExpressionTerm,
) : ExpressionTerm() {
    companion object {
        fun build(
            ctx: BinaryOperationAltContext,
        ): CallTerm {
            val leftExpression = ExpressionTerm.build(ctx.left)
            val rightExpression = ExpressionTerm.build(ctx.right)

            val prototype = BinaryOperationPrototype.build(ctx)

            return CallTerm(
                location = SourceLocation.build(ctx),
                subject = ReferenceTerm(
                    location = SourceLocation.build(ctx),
                    referee = Symbol.of(prototype.functionName),
                ),
                argument = UnorderedTupleLiteralTerm(
                    location = SourceLocation.build(ctx),
                    entries = listOf(
                        UnorderedTupleLiteralTerm.Entry(
                            name = prototype.leftArgument,
                            value = leftExpression,
                        ),
                        UnorderedTupleLiteralTerm.Entry(
                            name = prototype.rightArgument,
                            value = rightExpression,
                        ),
                    ),
                ),
            )
        }

        fun build(
            ctx: CallExpressionAltContext,
        ): CallTerm = CallTerm(
            location = SourceLocation.build(ctx),
            subject = ExpressionTerm.build(ctx.callee),
            argument = ExpressionTerm.build(ctx.argument),
        )

        fun build(
            ctx: CallExpressionTupleLiteralAltContext,
        ): CallTerm = CallTerm(
            location = SourceLocation.build(ctx),
            subject = ExpressionTerm.build(ctx.callee),
            argument = TupleLiteralTerm.build(ctx.argument),
        )
    }

    override fun validateAdditionally(
        typeScope: TypeScope,
        valueScope: SyntaxValueScope,
    ) {
        // TODO: Validate passed argument
    }

    override fun determineType(
        typeScope: TypeScope,
        valueScope: SyntaxValueScope,
    ): Type {
        val subjectType = subject.determineType(
            typeScope = typeScope,
            valueScope = valueScope,
        ) as? FunctionType ?: throw TypeErrorException(
            location = location,
            message = "Only functions can be called",
        )

        val argumentType = argument.determineType(
            typeScope = typeScope,
            valueScope = valueScope,
        )

        val typeVariableResolution = try {
            subjectType.argumentType.resolveTypeVariables(
                assignedType = argumentType
            )
        } catch (e: TypeVariableResolutionError) {
            throw TypeErrorException(
                location = location,
                message = e.message,
            )
        }

        return subjectType.imageType.substituteTypeVariables(
            resolution = typeVariableResolution,
        )
    }

    override fun evaluate(
        scope: Scope,
    ): Thunk {
        val subjectValue = subject.evaluate(scope = scope).toEvaluatedValue

        if (subjectValue !is FunctionValue) throw IllegalStateException("Subject $subjectValue is not a function")

        val argumentValue = argument.evaluate(scope = scope)

        // Thought: Obtaining argument here might not be lazy enough
        val image = subjectValue.apply(
            argument = argumentValue.toEvaluatedValue,
        )

        return image
    }

    override fun dump(): String = "${subject.dump()}[${argument.dump()}]"
}
