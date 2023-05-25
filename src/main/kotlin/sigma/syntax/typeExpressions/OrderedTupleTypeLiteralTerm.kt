package sigma.syntax.typeExpressions

import indexOfOrNull
import sigma.TypeScope
import sigma.Thunk
import sigma.syntax.SourceLocation
import sigma.parser.antlr.SigmaParser.OrderedTupleTypeLiteralContext
import sigma.semantics.types.OrderedTupleType
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.scope.Scope
import sigma.evaluation.values.tables.Table

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
        typeScope: TypeScope,
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
        override fun getValue(name: Symbol): Thunk? {
            val index = elements.indexOfOrNull { it.name == name } ?: return null

            return argument.read(IntValue(value = index.toLong()))
        }
    }
}
