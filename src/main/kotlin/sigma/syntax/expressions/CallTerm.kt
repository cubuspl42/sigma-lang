package sigma.syntax.expressions

import sigma.BinaryOperationPrototype

import sigma.evaluation.Thunk
import sigma.parser.antlr.SigmaParser.BinaryOperationAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionTupleConstructorAltContext
import sigma.syntax.SourceLocation
import sigma.evaluation.values.FunctionValue
import sigma.evaluation.values.Symbol
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
                argument = UnorderedTupleConstructorTerm(
                    location = SourceLocation.build(ctx),
                    entries = listOf(
                        UnorderedTupleConstructorTerm.Entry(
                            name = prototype.leftArgument,
                            value = leftExpression,
                        ),
                        UnorderedTupleConstructorTerm.Entry(
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
            ctx: CallExpressionTupleConstructorAltContext,
        ): CallTerm = CallTerm(
            location = SourceLocation.build(ctx),
            subject = ExpressionTerm.build(ctx.callee),
            argument = TupleConstructorTerm.build(ctx.argument),
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
