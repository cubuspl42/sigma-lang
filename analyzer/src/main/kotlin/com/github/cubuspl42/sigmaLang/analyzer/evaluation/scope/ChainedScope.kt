package com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value

class ChainedScope(
    private val outerScope: Scope,
    private val scope: Scope,
) : Scope {
    override fun getValue(
        name: Symbol,
    ): Thunk<Value>? = scope.getValue(
        name = name,
    ) ?: outerScope.getValue(
        name = name,
    )
}
