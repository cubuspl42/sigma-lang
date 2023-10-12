package com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.toThunk

class FixedDynamicScope(
    private val entries: Map<Identifier, Value>,
) : DynamicScope {
    override fun getValue(
        name: Identifier,
    ): Thunk<Value>? = entries[name]?.toThunk()
}
