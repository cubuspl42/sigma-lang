package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub
import com.github.cubuspl42.sigmaLang.shell.stubs.UnorderedTupleConstructorStub

data class UnorderedTupleConstructorTerm(
    val entries: List<Entry>,
) : ExpressionTerm {
    data class Entry(
        val key: IdentifierTerm,
        val value: ExpressionTerm,
    )

    companion object : Term.Builder<SigmaParser.UnorderedTupleConstructorContext, UnorderedTupleConstructorTerm>() {
        val Empty: UnorderedTupleConstructorTerm = UnorderedTupleConstructorTerm(
            entries = emptyList(),
        )

        override fun build(
            ctx: SigmaParser.UnorderedTupleConstructorContext,
        ): UnorderedTupleConstructorTerm = UnorderedTupleConstructorTerm(
            entries = ctx.unorderedTupleConstructorEntry().map {
                Entry(
                    key = IdentifierTerm.build(it.key),
                    value = ExpressionTerm.build(it.value),
                )
            },
        )

        override fun extract(parser: SigmaParser): SigmaParser.UnorderedTupleConstructorContext =
            parser.unorderedTupleConstructor()
    }

    override fun transmute() = UnorderedTupleConstructorStub(
         valueStubByKey = entries.associate { entry ->
             entry.key.transmute() to entry.value.transmute()
         }
     )
}
