package sigma.evaluation.values

import sigma.evaluation.scope.Scope
import sigma.semantics.expressions.EvaluationContext

sealed interface EvaluationResult

abstract class Value : EvaluationResult {
    abstract fun dump(): String
}

object EvaluationStackExhaustionError : EvaluationResult

abstract class Thunk() {
    private lateinit var cachedResult: EvaluationResult

    fun evaluate(
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
