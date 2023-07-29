package sigma.evaluation.values

import sigma.semantics.expressions.EvaluationContext

abstract class Value {
    abstract fun dump(): String

    val asEvaluationResult: EvaluationResult<Value>
        get() = EvaluationResult(value = this)

    inner class ValueAsThunk : Thunk<Value>() {
        override fun evaluate(
            context: EvaluationContext,
        ): EvaluationOutcome<Value> = this@Value.asEvaluationResult
    }

    val asThunk: ValueAsThunk
        get() = ValueAsThunk()
}
