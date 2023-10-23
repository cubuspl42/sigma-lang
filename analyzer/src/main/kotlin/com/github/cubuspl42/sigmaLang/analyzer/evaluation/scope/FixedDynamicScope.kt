package com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.toThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration

class FixedDynamicScope(
    private val entries: Map<Declaration, Value>,
) : DynamicScope {
    override fun getValue(
        declaration: Declaration,
    ): Thunk<Value>? = entries[declaration]?.toThunk()
}
