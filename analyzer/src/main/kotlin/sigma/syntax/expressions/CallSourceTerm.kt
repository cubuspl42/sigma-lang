package sigma.syntax.expressions

import sigma.BinaryOperationPrototype

import sigma.parser.antlr.SigmaParser.BinaryOperationAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionTupleConstructorAltContext
import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol

data class CallSourceTerm(
    override val location: SourceLocation,
    // Idea: Rename to `callee`? (again?)
    val subject: ExpressionSourceTerm,
    val argument: ExpressionSourceTerm,
) : ExpressionSourceTerm() {
    companion object {
        fun build(
            ctx: BinaryOperationAltContext,
        ): CallSourceTerm {
            val leftExpression = ExpressionSourceTerm.build(ctx.left)
            val rightExpression = ExpressionSourceTerm.build(ctx.right)

            val prototype = BinaryOperationPrototype.build(ctx)

            return CallSourceTerm(
                location = SourceLocation.build(ctx),
                subject = ReferenceSourceTerm(
                    location = SourceLocation.build(ctx),
                    referredName = Symbol.of(prototype.functionName),
                ),
                argument = UnorderedTupleConstructorSourceTerm(
                    location = SourceLocation.build(ctx),
                    entries = listOf(
                        UnorderedTupleConstructorSourceTerm.Entry(
                            name = prototype.leftArgument,
                            value = leftExpression,
                        ),
                        UnorderedTupleConstructorSourceTerm.Entry(
                            name = prototype.rightArgument,
                            value = rightExpression,
                        ),
                    ),
                ),
            )
        }

        fun build(
            ctx: CallExpressionAltContext,
        ): CallSourceTerm = CallSourceTerm(
            location = SourceLocation.build(ctx),
            subject = ExpressionSourceTerm.build(ctx.callee),
            argument = ExpressionSourceTerm.build(ctx.argument),
        )

        fun build(
            ctx: CallExpressionTupleConstructorAltContext,
        ): CallSourceTerm = CallSourceTerm(
            location = SourceLocation.build(ctx),
            subject = ExpressionSourceTerm.build(ctx.callee),
            argument = TupleConstructorSourceTerm.build(ctx.argument),
        )
    }

    override fun dump(): String = "${subject.dump()}[${argument.dump()}]"
}
