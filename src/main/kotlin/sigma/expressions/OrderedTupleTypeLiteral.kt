package sigma.expressions

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.Thunk
import sigma.TypeExpression
import sigma.parser.antlr.SigmaParser.OrderedTupleTypeLiteralContext
import sigma.types.OrderedTupleType
import sigma.types.TupleType
import sigma.types.Type
import sigma.values.IntValue
import sigma.values.Symbol
import sigma.values.tables.Scope
import sigma.values.tables.Table

data class OrderedTupleTypeLiteral(
    override val location: SourceLocation,
    val elements: List<Element>,
) : TupleTypeLiteral() {
    data class Element(
        val name: Symbol?,
        val type: TypeExpression,
    )

    companion object {
        fun build(
            ctx: OrderedTupleTypeLiteralContext,
        ): OrderedTupleTypeLiteral = OrderedTupleTypeLiteral(location = SourceLocation.build(ctx),
            elements = ctx.orderedTupleTypeElement().map { elementCtx ->
                Element(
                    name = elementCtx.name?.let { Symbol.of(it.text) },
                    type = TypeExpression.build(elementCtx.type),
                )
            })
    }

    override fun evaluate(
        typeScope: StaticTypeScope,
    ): OrderedTupleType = OrderedTupleType(
        elements = elements.map {
            OrderedTupleType.Element(
                name = it.name,
                type = it.type.evaluate(
                    typeScope = typeScope,
                ),
            )
        },
    )

    override fun toArgumentScope(argument: Table): Scope = object : Scope {
        override fun get(name: Symbol): Thunk? {
            val index = elements.withIndex().singleOrNull { (_, entry) ->
                name == entry.name
            }?.index ?: return null

            return argument.read(IntValue(index))
        }
    }
}
