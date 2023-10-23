package com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration

class ChainedDynamicScope(
    private val outerDynamicScope: DynamicScope,
    private val dynamicScope: DynamicScope,
) : DynamicScope {
    override fun getValue(
        declaration: Declaration,
    ): Thunk<Value>? = dynamicScope.getValue(
        declaration = declaration,
    ) ?: outerDynamicScope.getValue(
        declaration = declaration,
    )
}
