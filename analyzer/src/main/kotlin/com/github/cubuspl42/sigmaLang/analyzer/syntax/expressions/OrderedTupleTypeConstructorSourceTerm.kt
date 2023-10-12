package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.OrderedTupleTypeConstructorContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class OrderedTupleTypeConstructorSourceTerm(
    override val location: SourceLocation,
    override val elements: List<Element>,
) : TupleTypeConstructorSourceTerm(), OrderedTupleTypeConstructorTerm {
    data class Element(
        override val name: Identifier?,
        override val type: ExpressionTerm,
    ) : OrderedTupleTypeConstructorTerm.Element

    companion object {
        fun build(
            ctx: OrderedTupleTypeConstructorContext,
        ): OrderedTupleTypeConstructorSourceTerm = OrderedTupleTypeConstructorSourceTerm(
            location = SourceLocation.build(ctx),
            elements = ctx.orderedTupleTypeElement().map { elementCtx ->
                Element(
                    name = elementCtx.name?.let { Identifier.of(it.text) },
                    type = build(elementCtx.type),
                )
            },
        )
    }

    override fun dump(): String = "(ordered tuple type constructor)"
}
