package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.semantics.types.Type

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
    abstract val resolvedValue: Thunk<*>
}

class BuiltinResolution(
    val builtinValue: Value,
) : StaticResolution() {
    override val resolvedValue: Thunk<*>
        get() = builtinValue.asThunk
}

class ConstDefinitionResolution(
    private val constantDefinition: ConstantDefinition,
) : StaticResolution() {
    override val resolvedValue: Thunk<*>
        get() = constantDefinition.valueThunk
}

data class ResolvedName(
    val type: Type,
    val resolution: Resolution,
)