package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

data class ExpressionMap(
    private val map: Map<ExpressionTerm, Expression>,
) {
    fun getMappedExpression(
        term: ExpressionTerm,
    ): Expression? {
        return map[term]
    }

    fun unionWith(
        expressionMap: ExpressionMap,
    ): ExpressionMap = ExpressionMap(
        map = map + expressionMap.map,
    )

    companion object {
        val Empty = ExpressionMap(map = emptyMap())

        fun <E> unionAllOf(
            elements: Iterable<E>,
            extract: (E) -> ExpressionMap,
        ): ExpressionMap = elements.fold(
            initial = ExpressionMap.Empty,
        ) { acc, e ->
            acc.unionWith(extract(e))
        }
    }
}
