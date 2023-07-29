package sigma.evaluation.values

import sigma.semantics.expressions.EvaluationContext

interface Thunk<ValueType : Value> {
    fun evaluate(
        context: EvaluationContext,
    ): EvaluationResult

    fun evaluateValue(
        context: EvaluationContext,
    ): Value? = (evaluate(
        context = context,
    ) as? ValueResult)?.value
}

abstract class CachingThunk<ValueType : Value> : Thunk<ValueType> {
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
