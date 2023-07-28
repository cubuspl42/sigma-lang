package sigma.evaluation.values

import sigma.semantics.expressions.EvaluationContext

abstract class Value {
    abstract fun dump(): String

    val asEvaluationResult: ValueResult
        get() = ValueResult(value = this)

    val asThunk: ValueThunk
        get() = ValueThunk(value = this)
}

class ValueThunk(
    val value: Value,
) : Thunk {
    override fun evaluate(
        context: EvaluationContext,
    ): EvaluationResult = ValueResult(
        value = value,
    )
}
