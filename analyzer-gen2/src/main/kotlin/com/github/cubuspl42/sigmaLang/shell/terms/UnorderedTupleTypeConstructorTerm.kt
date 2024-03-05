package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor

data class UnorderedTupleTypeConstructorTerm(
    val body: UnorderedTupleConstructorTerm,
) : Term {
    companion object :
        Term.Builder<SigmaParser.UnorderedTupleTypeConstructorContext, UnorderedTupleTypeConstructorTerm>() {
        val Empty: UnorderedTupleTypeConstructorTerm = UnorderedTupleTypeConstructorTerm(
            body = UnorderedTupleConstructorTerm.Empty,
        )

        override fun build(
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

        override fun extract(parser: SigmaParser): SigmaParser.UnorderedTupleTypeConstructorContext =
            parser.unorderedTupleTypeConstructor()
    }

    constructor(
        entries: List<UnorderedTupleConstructorTerm.Entry>,
    ) : this(
        UnorderedTupleConstructorTerm(entries = entries),
    )

    val keys: Set<IdentifierTerm> = body.entries.map { it.key }.toSet()
}
