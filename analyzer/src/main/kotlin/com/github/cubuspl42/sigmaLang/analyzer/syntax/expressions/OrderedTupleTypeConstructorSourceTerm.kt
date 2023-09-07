package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.OrderedTupleTypeConstructorContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Constness
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class OrderedTupleTypeConstructorSourceTerm(
    override val constness: Constness = Constness.Variable,
    override val location: SourceLocation,
    override val elements: List<Element>,
) : TupleTypeConstructorSourceTerm(), OrderedTupleTypeConstructorTerm {
    data class Element(
        override val name: Symbol?,
        override val type: ExpressionTerm,
    ) : OrderedTupleTypeConstructorTerm.Element

    companion object {
        fun build(
            location: SourceLocation,
            constness: Constness,
            ctx: OrderedTupleTypeConstructorContext,
        ): OrderedTupleTypeConstructorSourceTerm = OrderedTupleTypeConstructorSourceTerm(
            location = location,
            constness = constness,
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
