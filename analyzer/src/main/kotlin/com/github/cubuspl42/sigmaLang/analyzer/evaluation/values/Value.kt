package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.EvaluationContext

sealed class Value {
    abstract fun dump(): String
}

val <ResultType> ResultType.asEvaluationResult: EvaluationResult<ResultType>
    get() = EvaluationResult(value = this)

val <ResultType> ResultType.asThunk: Thunk<ResultType>
    get() = object : Thunk<ResultType>() {
        override fun evaluateDirectly(
            context: EvaluationContext,
        ): EvaluationOutcome<ResultType> = this@asThunk.asEvaluationResult
    }
