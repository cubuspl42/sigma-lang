package com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration

interface DynamicScope {
    object Empty : DynamicScope {
        override fun getValue(name: Declaration): Thunk<Value>? = null
    }

    fun getValue(
        name: Declaration,
    ): Thunk<Value>?
}

fun DynamicScope.chainWith(
    context: DynamicScope,
): DynamicScope = ChainedDynamicScope(
    outerDynamicScope = context,
    dynamicScope = this,
)
