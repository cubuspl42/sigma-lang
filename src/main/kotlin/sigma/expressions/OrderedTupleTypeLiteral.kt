package sigma.expressions

import sigma.StaticTypeScope
import sigma.TypeExpression
import sigma.parser.antlr.SigmaParser.TupleContext
import sigma.types.OrderedTupleType
import sigma.types.Type
import sigma.values.Symbol

data class OrderedTupleTypeLiteral(
    override val location: SourceLocation,
    val entries: List<EntryExpression>,
) : TypeExpression() {
    data class EntryExpression(
        val name: Symbol?,
        val valueType: TypeExpression,
    )

    companion object {
        fun build(
            ctx: TupleContext,
        ): OrderedTupleTypeLiteral = TODO()
    }

    override fun evaluate(
        typeScope: StaticTypeScope,
    ): Type = OrderedTupleType(
        entries = entries.map {
            OrderedTupleType.Entry(
                name = it.name,
                elementType = it.valueType.evaluate(
                    typeScope = typeScope,
                ),
            )
        },
    )
}
