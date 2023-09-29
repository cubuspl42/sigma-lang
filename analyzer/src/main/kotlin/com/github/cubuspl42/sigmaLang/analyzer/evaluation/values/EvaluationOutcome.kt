package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

sealed interface EvaluationOutcome<out ResultType>

data class EvaluationResult<ResultType>(
    val value: ResultType,
): EvaluationOutcome<ResultType>

sealed interface EvaluationError : EvaluationOutcome<Nothing>

data object EvaluationStackExhaustionError : EvaluationError
