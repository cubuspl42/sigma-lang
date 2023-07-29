package sigma.evaluation.values

import sigma.semantics.expressions.EvaluationContext

abstract class Value {
    abstract fun dump(): String
}

val <ValueType : Value> ValueType.asEvaluationResult: EvaluationResult<ValueType>
    get() = EvaluationResult(value = this)

val <ValueType : Value> ValueType.asThunk: Thunk<ValueType>
    get() = object : Thunk<ValueType>() {
        override fun evaluateDirectly(
            context: EvaluationContext,
        ): EvaluationOutcome<ValueType> = this@asThunk.asEvaluationResult
    }
