package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asThunk
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

sealed class Resolution {}

class DynamicResolution(
    val resolvedFormula: Formula?,
) : Resolution()

abstract class StaticResolution : Resolution() {
    abstract val resolvedValue: Thunk<Value>
}

class BuiltinResolution(
    val builtinValue: Value,
) : StaticResolution() {
    override val resolvedValue: Thunk<Value>
        get() = builtinValue.asThunk
}

class ConstDefinitionResolution(
    private val constantDefinition: ConstantDefinition,
) : StaticResolution() {
    override val resolvedValue: Thunk<Value>
        get() = constantDefinition.valueThunk
}

data class ResolvedName(
    val type: Thunk<Type>,
    val resolution: Resolution,
)