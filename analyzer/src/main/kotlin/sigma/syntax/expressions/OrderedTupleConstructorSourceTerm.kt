package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.OrderedTupleConstructorContext
import sigma.syntax.SourceLocation

data class OrderedTupleConstructorSourceTerm(
    override val location: SourceLocation,
    override val elements: List<ExpressionSourceTerm>,
) : TupleConstructorSourceTerm(), OrderedTupleConstructorTerm {
    companion object {
        fun build(
            ctx: OrderedTupleConstructorContext,
        ): OrderedTupleConstructorSourceTerm = OrderedTupleConstructorSourceTerm(
            location = SourceLocation.build(ctx),
            elements = ctx.orderedTupleElement().map {
                ExpressionSourceTerm.build(it)
            },
        )
    }

    override fun dump(): String = "(dict constructor)"
}
