package sigma.syntax.expressions

import sigma.evaluation.values.Symbol
import sigma.parser.antlr.SigmaParser.OrderedTupleTypeConstructorContext
import sigma.syntax.SourceLocation

data class OrderedTupleTypeConstructorSourceTerm(
    override val location: SourceLocation,
    val elements: List<Element>,
) : TupleTypeConstructorSourceTerm() {
    data class Element(
        val name: Symbol?,
        val type: ExpressionSourceTerm,
    )

    companion object {
        fun build(
            ctx: OrderedTupleTypeConstructorContext,
        ): OrderedTupleTypeConstructorSourceTerm = OrderedTupleTypeConstructorSourceTerm(
            location = SourceLocation.build(ctx),
            elements = ctx.orderedTupleTypeElement().map { elementCtx ->
                Element(
                    name = elementCtx.name?.let { Symbol.of(it.text) },
                    type = build(elementCtx.type),
                )
            },
        )
    }

//    override fun evaluate(
//        declarationScope: StaticScope,
//    ): OrderedTupleType = OrderedTupleType(
//        elements = elements.map {
//            OrderedTupleType.Element(
//                name = it.name,
//                type = it.type.evaluateAsType(
//                    declarationScope = declarationScope,
//                ),
//            )
//        },
//    )

    override fun dump(): String = "(ordered tuple type constructor)"
}
