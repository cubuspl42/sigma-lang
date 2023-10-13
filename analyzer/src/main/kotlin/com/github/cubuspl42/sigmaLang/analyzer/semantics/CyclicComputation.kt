package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression

abstract class CyclicComputationClass<Result> {
    fun visiting(
        expression: Expression,
    ): CyclicComputation<Result> = object : CyclicComputation<Result>() {
        override fun compute(context: CyclicComputation.Context): Result? {
            if (context.wasVisited(expression)) {
                return null
            }

            val innerContext = context.withVisited(expression)

            return mergeResults(
                expression.subExpressions.mapNotNull { subExpression ->
                    process(expression = subExpression).compute(innerContext)
                },
            )
        }
    }

    abstract fun process(
        expression: Expression,
    ): CyclicComputation<Result>

    abstract fun mergeResults(
        results: Iterable<Result>,
    ): Result
}

abstract class CyclicComputation<Result> {
    data class Context(
        val visitedExpressions: Set<Expression>,
    ) {
        companion object {
            val Empty = Context(
                visitedExpressions = emptySet(),
            )
        }

        fun wasVisited(expression: Expression): Boolean = expression in visitedExpressions

        fun withVisited(expression: Expression): Context = copy(
            visitedExpressions = visitedExpressions + expression,
        )
    }

    fun getOrCompute(): Result = compute(
        context = Context.Empty,
    )!!

    abstract fun compute(
        context: Context,
    ): Result?
}
