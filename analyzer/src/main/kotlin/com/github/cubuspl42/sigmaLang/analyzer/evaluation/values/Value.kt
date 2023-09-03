package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.EvaluationContext

interface Value {
    fun dump(): String
}

val <ValueType : Value> ValueType.asEvaluationResult: EvaluationResult<ValueType>
    get() = EvaluationResult(value = this)

val <ValueType : Value> ValueType.asThunk: Thunk<ValueType>
    get() = object : Thunk<ValueType>() {
        override fun evaluateDirectly(
            context: EvaluationContext,
        ): EvaluationOutcome<ValueType> = this@asThunk.asEvaluationResult
    }
