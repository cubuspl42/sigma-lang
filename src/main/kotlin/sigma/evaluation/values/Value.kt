package sigma.evaluation.values

abstract class Value {
    abstract fun dump(): String

    val asEvaluationResult: ValueResult
        get() = ValueResult(value = this)
}
