package sigma.evaluation.values

sealed interface EvaluationOutcome<out ResultType>

data class EvaluationResult<ResultType>(
    val value: ResultType,
): EvaluationOutcome<ResultType>

sealed interface EvaluationError : EvaluationOutcome<Nothing>

object EvaluationStackExhaustionError : EvaluationError
