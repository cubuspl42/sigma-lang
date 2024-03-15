package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.Value

data class UnorderedTupleTypeConstructorTerm(
    override val names: Set<Identifier>,
) : TupleTypeConstructorTerm() {
    companion object :
        Term.Builder<SigmaParser.UnorderedTupleTypeConstructorContext, UnorderedTupleTypeConstructorTerm>() {
        val Empty: UnorderedTupleTypeConstructorTerm = UnorderedTupleTypeConstructorTerm(
            names = emptySet(),
        )

        override fun build(
            ctx: SigmaParser.UnorderedTupleTypeConstructorContext,
        ): UnorderedTupleTypeConstructorTerm = UnorderedTupleTypeConstructorTerm(
            names = ctx.unorderedTupleTypeConstructorEntry().map {
                IdentifierTerm.build(it.key).toIdentifier()
            }.toSet(),
        )

        override fun extract(parser: SigmaParser): SigmaParser.UnorderedTupleTypeConstructorContext =
            parser.unorderedTupleTypeConstructor()
    }

    override fun wrap(): Value = TODO()
}
