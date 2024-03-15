package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.Value

data class OrderedTupleTypeConstructorTerm(
    override val names: List<Identifier>,
) : TupleTypeConstructorTerm() {
    companion object : Term.Builder<SigmaParser.OrderedTupleTypeConstructorContext, OrderedTupleTypeConstructorTerm>() {
        val Empty: OrderedTupleTypeConstructorTerm = OrderedTupleTypeConstructorTerm(
            names = emptyList(),
        )

        override fun build(
            ctx: SigmaParser.OrderedTupleTypeConstructorContext,
        ): OrderedTupleTypeConstructorTerm = OrderedTupleTypeConstructorTerm(
            names = ctx.orderedTupleTypeConstructorEntry().map {
                IdentifierTerm.build(it.key).toIdentifier()
            },
        )

        override fun extract(parser: SigmaParser): SigmaParser.OrderedTupleTypeConstructorContext =
            parser.orderedTupleTypeConstructor()
    }

    override fun wrap(): Value = TODO()
}
