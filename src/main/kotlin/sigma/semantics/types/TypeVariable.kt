package sigma.semantics.types

import sigma.evaluation.values.Symbol
import sigma.semantics.Formula

data class TypeVariable(
    // TODO
    val formula: Formula,
) : Type() {
    override fun findLowestCommonSupertype(
        other: Type,
    ): Type = AnyType

    override fun resolveTypeVariables(
        assignedType: Type,
    ): TypeVariableResolution = TypeVariableResolution(
        resolvedTypeByVariable = mapOf(this to assignedType),
    )

    // Thought: Return an error if resolution misses this variable?
    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): Type = resolution.resolvedTypeByVariable[this] ?: this

    override fun match(assignedType: Type): MatchResult = when (assignedType) {
        this -> TotalMatch
        else -> TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }

    override fun dump(): String = "#${formula.name}"
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

data class TypeVariableResolutionError(
    override val message: String,
) : Exception()
