package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.OrderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.values.ListValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.TransmutationContext

data class OrderedTupleConstructorTerm(
    val elements: List<ExpressionTerm>,
) : TupleConstructorTerm() {
    companion object : Term.Builder<SigmaParser.OrderedTupleConstructorContext, OrderedTupleConstructorTerm>() {
        val Empty: OrderedTupleConstructorTerm = OrderedTupleConstructorTerm(
            elements = emptyList(),
        )

        override fun build(
            ctx: SigmaParser.OrderedTupleConstructorContext,
        ): OrderedTupleConstructorTerm = OrderedTupleConstructorTerm(
            elements = ctx.expression().map {
                ExpressionTerm.build(it)
            },
        )

        override fun extract(parser: SigmaParser): SigmaParser.OrderedTupleConstructorContext =
            parser.orderedTupleConstructor()
    }
    override fun transmute(
        context: TransmutationContext,
    ): Expression = OrderedTupleConstructor(
        elements = elements.map {
            lazyOf(
                it.transmute(
                    context = context,
                ),
            )
        },
    )

    override fun wrap(): Value = ListValue(
        values = elements.map { entry -> entry.wrap() },
    )
}
