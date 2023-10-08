package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ConstantDefinition

// Thought: Figure out better names
class ReverseTranslationScope(
    private val staticScope: StaticScope,
) : DynamicScope {
    override fun getValue(name: Symbol): Thunk<Value>? =
        staticScope.resolveName(name = name)?.let { resolvableDeclaration ->
            if (resolvableDeclaration is ConstantDefinition) {
                resolvableDeclaration.valueThunk
            } else {
                null
            }
        }
}
