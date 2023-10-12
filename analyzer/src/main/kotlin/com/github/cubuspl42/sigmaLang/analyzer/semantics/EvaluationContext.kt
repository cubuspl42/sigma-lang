package com.github.cubuspl42.sigmaLang.analyzer.semantics

data class EvaluationContext(
    val evaluationDepth: Int,
) {
    companion object {
        val Initial: EvaluationContext = EvaluationContext(
            evaluationDepth = 0,
        )

        const val maxEvaluationDepth: Int = 2048
    }

    fun withIncreasedDepth(): EvaluationContext = EvaluationContext(
        evaluationDepth = evaluationDepth + 1,
    )
}
