package sigma.evaluation.values

sealed interface EvaluationResult

data class ValueResult(
    val value: Value,
): EvaluationResult

object EvaluationStackExhaustionError : EvaluationResult
