package com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value

interface Scope {
    object Empty : Scope {
        override fun getValue(name: Symbol): Thunk<Value>? = null
    }

    fun getValue(
        name: Symbol,
    ): Thunk<Value>?
}

fun Scope.chainWith(
    context: Scope,
): Scope = ChainedScope(
    outerScope = context,
    scope = this,
)
