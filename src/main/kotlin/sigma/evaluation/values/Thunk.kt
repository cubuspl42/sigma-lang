package sigma.evaluation.values

import sigma.semantics.expressions.EvaluationContext

interface Thunk {
    fun evaluate(
        context: EvaluationContext,
    ): EvaluationResult
}

abstract class CachingThunk : Thunk {
    private lateinit var cachedResult: EvaluationResult
    override fun evaluate(
        context: EvaluationContext,
    ): EvaluationResult = if (this::cachedResult.isInitialized) {
        this.cachedResult
    } else {
        val result = this.evaluateDirectly(
            context = context,
        )

        this.cachedResult = result

        result
    }

    abstract fun evaluateDirectly(
        context: EvaluationContext,
    ): EvaluationResult
}
