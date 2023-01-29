package sigma.types

// TODO: Identify type variables somehow
object TypeVariable : Type() {
    override fun resolveTypeVariables(
        assignedType: Type,
    ): TypeVariableResolution = TypeVariableResolution(
        resolvedTypeByVariable = mapOf(this to assignedType),
    )

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): Type = resolution.resolvedTypeByVariable[this] ?: this

    override fun dump(): String = "#T"
}

data class TypeVariableResolution(
    val resolvedTypeByVariable: Map<TypeVariable, Type>,
) {
    fun mergeWith(
        other: TypeVariableResolution,
    ): TypeVariableResolution {
        // TODO: Check for resolution incompatibilities
        return TypeVariableResolution(
            resolvedTypeByVariable = resolvedTypeByVariable + other.resolvedTypeByVariable,
        )
    }

    companion object {
        val Empty = TypeVariableResolution(
            resolvedTypeByVariable = emptyMap(),
        )
    }
}
