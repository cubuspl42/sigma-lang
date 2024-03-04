package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser

data class UnorderedTupleTypeConstructorTerm(
    val body: UnorderedTupleConstructorTerm,
) : Term {
    companion object {
        val Empty: UnorderedTupleTypeConstructorTerm = UnorderedTupleTypeConstructorTerm(
            body = UnorderedTupleConstructorTerm.Empty,
        )

        fun build(
            ctx: SigmaParser.UnorderedTupleTypeConstructorContext,
        ): UnorderedTupleTypeConstructorTerm = UnorderedTupleTypeConstructorTerm(
            body = UnorderedTupleConstructorTerm(
                entries = ctx.unorderedTupleTypeConstructorEntry().map {
                    UnorderedTupleConstructorTerm.Entry(
                        key = IdentifierTerm.build(it.key),
                        value = ExpressionTerm.build(it.value),
                    )
                },
            ),
        )
    }

    val keys: Set<IdentifierTerm> = body.entries.map { it.key }.toSet()
}
