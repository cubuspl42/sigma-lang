package com.github.cubuspl42.sigmaLang.shell

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser

data class UnorderedTupleConstructorTerm(
    val entries: List<Entry>,
) : ExpressionTerm {
    data class Entry(
        val key: IdentifierTerm,
        val value: ExpressionTerm,
    )

    companion object {
        fun build(
            ctx: SigmaParser.UnorderedTupleConstructorContext,
        ): UnorderedTupleConstructorTerm = UnorderedTupleConstructorTerm(
            entries = ctx.unorderedTupleConstructorEntry().map {
                Entry(
                    key = IdentifierTerm.build(it.key),
                    value = ExpressionTerm.build(it.value),
                )
            },
        )
    }
}
