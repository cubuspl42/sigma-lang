package sigma.syntax.expressions

import sigma.TypeScope
import sigma.SyntaxValueScope
import sigma.parser.antlr.SigmaParser.OrderedTupleConstructorContext
import sigma.syntax.SourceLocation
import sigma.semantics.types.OrderedTupleType
import sigma.evaluation.scope.Scope
import sigma.semantics.types.Type
import sigma.evaluation.values.tables.ArrayTable

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

    override fun determineType(
        typeScope: TypeScope,
        valueScope: SyntaxValueScope,
    ): Type = OrderedTupleType(
        elements = elements.map {
            val type = it.determineType(
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
