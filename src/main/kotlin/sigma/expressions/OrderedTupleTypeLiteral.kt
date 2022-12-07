package sigma.expressions

import sigma.StaticTypeScope
import sigma.TypeExpression
import sigma.parser.antlr.SigmaParser.OrderedTupleTypeLiteralContext
import sigma.types.OrderedTupleType
import sigma.types.Type
import sigma.values.Symbol

data class OrderedTupleTypeLiteral(
    override val location: SourceLocation,
    val entries: List<Element>,
) : TypeExpression() {
    data class Element(
        val name: Symbol?,
        val type: TypeExpression,
    )

    companion object {
        fun build(
            ctx: OrderedTupleTypeLiteralContext,
        ): OrderedTupleTypeLiteral = OrderedTupleTypeLiteral(
            location = SourceLocation.build(ctx),
            entries = ctx.orderedTupleTypeElement().map { elementCtx ->
                Element(
                    name = elementCtx.name?.let { Symbol.of(it.text) },
                    type = TypeExpression.build(elementCtx.type),
                )
            }
        )
    }

    override fun evaluate(
        typeScope: StaticTypeScope,
    ): Type = OrderedTupleType(
        entries = entries.map {
            OrderedTupleType.Entry(
                name = it.name,
                elementType = it.type.evaluate(
                    typeScope = typeScope,
                ),
            )
        },
    )
}
