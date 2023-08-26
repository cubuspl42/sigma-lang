package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.DynamicResolution
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticResolution
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.MetaType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable

class TranslationScope(
    private val staticScope: StaticScope,
) : Scope {
    override fun getValue(
        name: Symbol,
    ): Thunk<Value>? = staticScope.resolveName(
        name = name,
    )?.let { resolvedName ->
        when (val resolution = resolvedName.resolution) {
            is StaticResolution -> resolution.resolvedValue

            is DynamicResolution -> when (resolvedName.type.value) {
                is MetaType -> TypeVariable(
                    // FIXME
                    formula = resolution.resolvedFormula ?: Formula(name = Symbol.of("?")),
                ).asThunk

                else -> null
            }
        }
    }
}