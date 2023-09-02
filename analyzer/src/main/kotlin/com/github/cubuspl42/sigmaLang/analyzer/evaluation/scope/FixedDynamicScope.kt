package com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asThunk

class FixedDynamicScope(
    private val entries: Map<Symbol, Value>,
) : DynamicScope {
    override fun getValue(
        name: Symbol,
    ): Thunk<Value>? = entries[name]?.asThunk
}
