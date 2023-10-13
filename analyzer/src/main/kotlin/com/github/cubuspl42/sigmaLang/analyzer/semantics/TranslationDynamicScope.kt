package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ConstantDefinition

class TranslationDynamicScope(
    private val staticScope: StaticScope,
) : DynamicScope {
    override fun getValue(
        name: Symbol,
    ): Thunk<Value>? = staticScope.resolveName(
        name = name,
    )?.let { resolvedIntroduction ->
        when (resolvedIntroduction) {
            is ConstantDefinition -> resolvedIntroduction.valueThunk

            else -> null
        }
    }
}
