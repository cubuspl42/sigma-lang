package com.github.cubuspl42.sigmaLang

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser

data class FormExpression(
    val entries: List<Entry>,
) : Expression {
    data class Entry(
        val key: Expression,
        val value: Expression,
    ) {
        fun dump(): String = "${key.dump()}: ${value.dump()}"
    }

    companion object {
        val empty = FormExpression(
            entries = emptyList(),
        )

        fun build(
            expression: SigmaParser.FormContext,
        ): FormExpression = FormExpression(
            entries = expression.entry().map {
                Entry(
                    key = Expression.build(it.key),
                    value = Expression.build(it.value),
                )
            },
        )
    }

    override fun dump(): String = "{${entries.joinToString { it.dump() }}}"
}
