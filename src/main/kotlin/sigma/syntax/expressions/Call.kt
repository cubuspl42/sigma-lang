package sigma.syntax.expressions

import sigma.BinaryOperationPrototype
import sigma.StaticTypeScope
import sigma.StaticValueScope

import sigma.Thunk
import sigma.parser.antlr.SigmaParser.BinaryOperationAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionTupleLiteralAltContext
import sigma.syntax.SourceLocation
import sigma.semantics.types.FunctionType
import sigma.semantics.types.Type
import sigma.semantics.types.TypeVariableResolutionError
import sigma.values.FunctionValue
import sigma.values.Symbol
import sigma.values.TypeError
import sigma.values.tables.Scope

data class Call(
    override val location: SourceLocation,
    // Idea: Rename to `callee`? (again?)
    val subject: Expression,
    val argument: Expression,
) : Expression() {
    companion object {
        fun build(
            ctx: BinaryOperationAltContext,
        ): Call {
            val leftExpression = Expression.build(ctx.left)
            val rightExpression = Expression.build(ctx.right)

            val prototype = BinaryOperationPrototype.build(ctx)

            return Call(
                location = SourceLocation.build(ctx),
                subject = Reference(
                    location = SourceLocation.build(ctx),
                    referee = Symbol.of(prototype.functionName),
                ),
                argument = UnorderedTupleLiteral(
                    location = SourceLocation.build(ctx),
                    entries = listOf(
                        UnorderedTupleLiteral.Entry(
                            name = prototype.leftArgument,
                            value = leftExpression,
                        ),
                        UnorderedTupleLiteral.Entry(
                            name = prototype.rightArgument,
                            value = rightExpression,
                        ),
                    ),
                ),
            )
        }

        fun build(
            ctx: CallExpressionAltContext,
        ): Call = Call(
            location = SourceLocation.build(ctx),
            subject = Expression.build(ctx.callee),
            argument = Expression.build(ctx.argument),
        )

        fun build(
            ctx: CallExpressionTupleLiteralAltContext,
        ): Call = Call(
            location = SourceLocation.build(ctx),
            subject = Expression.build(ctx.callee),
            argument = TupleLiteral.build(ctx.argument),
        )
    }

    override fun validateAdditionally(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ) {
        // TODO: Validate passed argument
    }

    override fun determineType(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ): Type {
        val subjectType = subject.determineType(
            typeScope = typeScope,
            valueScope = valueScope,
        ) as? FunctionType ?: throw TypeError(
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
            throw TypeError(
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
