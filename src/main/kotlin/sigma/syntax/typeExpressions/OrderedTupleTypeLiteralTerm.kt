package sigma.syntax.typeExpressions

import indexOfOrNull
import sigma.SyntaxTypeScope
import sigma.Thunk
import sigma.syntax.SourceLocation
import sigma.parser.antlr.SigmaParser.OrderedTupleTypeLiteralContext
import sigma.semantics.types.OrderedTupleType
import sigma.values.IntValue
import sigma.values.Symbol
import sigma.values.tables.Scope
import sigma.values.tables.Table

data class OrderedTupleTypeLiteralTerm(
    override val location: SourceLocation,
    val elements: List<Element>,
) : TupleTypeLiteralTerm() {
    data class Element(
        val name: Symbol?,
        val type: TypeExpressionTerm,
    )

    companion object {
        fun build(
            ctx: OrderedTupleTypeLiteralContext,
        ): OrderedTupleTypeLiteralTerm = OrderedTupleTypeLiteralTerm(
            location = SourceLocation.build(ctx),
            elements = ctx.orderedTupleTypeElement().map { elementCtx ->
                Element(
                    name = elementCtx.name?.let { Symbol.of(it.text) },
                    type = build(elementCtx.type),
                )
            },
        )
    }

    override fun evaluate(
        typeScope: SyntaxTypeScope,
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
            val index = elements.indexOfOrNull { it.name == name } ?: return null

            return argument.read(IntValue(value = index.toLong()))
        }
    }
}
