package com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value

interface DynamicScope {
    object Empty : DynamicScope {
        override fun getValue(name: Symbol): Thunk<Value>? = null
    }

    fun getValue(
        name: Symbol,
    ): Thunk<Value>?
}

fun DynamicScope.chainWith(
    context: DynamicScope,
): DynamicScope = ChainedDynamicScope(
    outerDynamicScope = context,
    dynamicScope = this,
)