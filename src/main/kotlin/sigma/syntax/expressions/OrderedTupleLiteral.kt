package sigma.syntax.expressions

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.parser.antlr.SigmaParser.OrderedTupleLiteralContext
import sigma.syntax.SourceLocation
import sigma.semantics.types.OrderedTupleType
import sigma.values.tables.Scope
import sigma.semantics.types.Type
import sigma.values.tables.ArrayTable

data class OrderedTupleLiteral(
    override val location: SourceLocation,
    val elements: List<Expression>,
) : TupleLiteral() {
    companion object {
        fun build(
            ctx: OrderedTupleLiteralContext,
        ): OrderedTupleLiteral = OrderedTupleLiteral(
            location = SourceLocation.build(ctx),
            elements = ctx.orderedTupleElement().map {
                Expression.build(it)
            },
        )
    }

    override fun dump(): String = "(dict constructor)"

    override fun validateAndInferType(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ): Type = OrderedTupleType(
        elements = elements.map {
            val type = it.validateAndInferType(
                typeScope = typeScope,
                valueScope = valueScope,
            )

            OrderedTupleType.Element(
                name = null,
                type = type,
            )
        },
    )

    override fun evaluate(
        scope: Scope,
    ) = ArrayTable(
        elements = elements.map {
            it.evaluate(scope = scope)
        },
    )
}
