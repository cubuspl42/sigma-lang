package sigma.evaluation.values

sealed interface EvaluationResult

abstract class Value : EvaluationResult {
    abstract fun dump(): String
}

object CallStackExhaustionError : EvaluationResult
