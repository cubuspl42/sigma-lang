package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.shell.ConstructionContext

data class UnorderedTupleConstructorTerm(
    val entries: List<Entry>,
) : ExpressionTerm {
    data class Entry(
        val key: IdentifierTerm,
        val value: ExpressionTerm,
    )

    companion object {
        val Empty: UnorderedTupleConstructorTerm = UnorderedTupleConstructorTerm(
            entries = emptyList(),
        )

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

    override fun construct(context: ConstructionContext): Lazy<Expression> = lazyOf(
        UnorderedTupleConstructor(
            valueByKey = entries.associate {
                it.key.construct() to it.value.construct(context)
            },
        )
    )
}
