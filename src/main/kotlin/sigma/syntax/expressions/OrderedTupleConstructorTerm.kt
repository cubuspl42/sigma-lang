package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.OrderedTupleConstructorContext
import sigma.syntax.SourceLocation

data class OrderedTupleConstructorTerm(
    override val location: SourceLocation,
    val elements: List<ExpressionTerm>,
) : TupleConstructorTerm() {
    companion object {
        fun build(
            ctx: OrderedTupleConstructorContext,
        ): OrderedTupleConstructorTerm = OrderedTupleConstructorTerm(
            location = SourceLocation.build(ctx),
            elements = ctx.orderedTupleElement().map {
                ExpressionTerm.build(it)
            },
        )
    }

    override fun dump(): String = "(dict constructor)"
}
