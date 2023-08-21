package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.OrderedTupleTypeConstructorContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class OrderedTupleTypeConstructorSourceTerm(
    override val location: SourceLocation,
    override val elements: List<Element>,
) : TupleTypeConstructorSourceTerm(), OrderedTupleTypeConstructorTerm {
    data class Element(
        override val name: Symbol?,
        override val type: ExpressionSourceTerm,
    ) : OrderedTupleTypeConstructorTerm.Element

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

    override fun dump(): String = "(ordered tuple type constructor)"
}
