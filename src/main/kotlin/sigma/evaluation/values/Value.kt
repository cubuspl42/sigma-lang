package sigma.evaluation.values

import sigma.semantics.expressions.EvaluationContext

abstract class Value {
    abstract fun dump(): String

    val asEvaluationResult: ValueResult
        get() = ValueResult(value = this)

    val asThunk: ValueAsThunk
        get() = ValueAsThunk(value = this)
}

class ValueAsThunk(
    val value: Value,
) : Thunk<Value> {
    override fun evaluate(
        context: EvaluationContext,
    ): EvaluationResult = ValueResult(
        value = value,
    )
}
