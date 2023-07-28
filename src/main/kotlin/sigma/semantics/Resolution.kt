package sigma.semantics

import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.semantics.types.Type

sealed interface Formula

sealed class Resolution {
}

class DynamicResolution() : Resolution()

abstract class StaticResolution : Resolution() {
    abstract val resolvedValue: Thunk
}

class BuiltinResolution(
    val builtinValue: Value,
) : StaticResolution() {
    override val resolvedValue: Thunk
        get() = builtinValue.asThunk
}

class ConstDefinitionResolution(
    private val constantDefinition: ConstantDefinition,
) : StaticResolution() {
    override val resolvedValue: Thunk
        get() = constantDefinition.valueThunk
}

data class ResolvedName(
    val type: Type,
    val resolution: Resolution,
)