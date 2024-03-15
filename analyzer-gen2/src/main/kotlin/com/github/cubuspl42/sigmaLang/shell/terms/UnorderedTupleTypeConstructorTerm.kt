package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.core.values.Value

data class UnorderedTupleTypeConstructorTerm(
    val keys: Set<Identifier>,
) : Term {
    companion object :
        Term.Builder<SigmaParser.UnorderedTupleTypeConstructorContext, UnorderedTupleTypeConstructorTerm>() {
        val Empty: UnorderedTupleTypeConstructorTerm = UnorderedTupleTypeConstructorTerm(
            keys = emptySet(),
        )

        override fun build(
            ctx: SigmaParser.UnorderedTupleTypeConstructorContext,
        ): UnorderedTupleTypeConstructorTerm = UnorderedTupleTypeConstructorTerm(
            keys = ctx.unorderedTupleTypeConstructorEntry().map {
                IdentifierTerm.build(it.key).toIdentifier()
            }.toSet(),
        )

        override fun extract(parser: SigmaParser): SigmaParser.UnorderedTupleTypeConstructorContext =
            parser.unorderedTupleTypeConstructor()
    }

    override fun wrap(): Value = TODO()
}
