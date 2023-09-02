package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type

data class Formula(
    val name: Symbol,
) {
    companion object {
        fun of(s: String): Formula = Formula(
            name = Symbol.of(s)
        )
    }
}

sealed class Resolution

class DynamicResolution(
    val resolvedFormula: Formula?,
) : Resolution()

class StaticResolution(
    private val namespaceEntry: NamespaceEntry,
) : Resolution() {
    val resolvedValue: Thunk<Value> = namespaceEntry.valueThunk
}

data class ResolvedName(
    val type: Thunk<Type>,
    val resolution: Resolution,
)
