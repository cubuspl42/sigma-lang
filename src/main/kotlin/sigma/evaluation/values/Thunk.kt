package sigma.evaluation.values

import sigma.semantics.expressions.EvaluationContext

interface Thunk<ValueType : Value> {
    fun evaluate(
        context: EvaluationContext,
    ): EvaluationResult

    // TODO: Nuke
    fun evaluateValueHacky(
        context: EvaluationContext,
    ): Value? = (evaluate(
        context = context,
    ) as? ValueResult)?.value

    fun evaluateInitial(): EvaluationResult = evaluate(
        context = EvaluationContext.Initial,
    )

    fun evaluateInitialValue() = (evaluateInitial() as ValueResult).value
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
