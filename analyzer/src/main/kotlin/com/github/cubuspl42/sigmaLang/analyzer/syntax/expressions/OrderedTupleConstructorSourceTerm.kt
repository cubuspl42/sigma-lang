package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.OrderedTupleConstructorContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class OrderedTupleConstructorSourceTerm(
    override val location: SourceLocation,
    override val elements: List<ExpressionTerm>,
) : TupleConstructorSourceTerm(), OrderedTupleConstructorTerm {
    companion object {
        fun build(
            ctx: OrderedTupleConstructorContext,
        ): OrderedTupleConstructorSourceTerm = OrderedTupleConstructorSourceTerm(
            location = SourceLocation.build(ctx),
            elements = ctx.orderedTupleElement().map {
                ExpressionSourceTerm.build(it)
            },
        )
    }

    override fun dump(): String = "(dict constructor)"
}
