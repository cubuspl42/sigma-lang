package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula

data class TypeVariable(
    // TODO
    val formula: Formula,
) : Type() {
    companion object {
        fun of(name: String) = TypeVariable(
            formula = Formula.of(name),
        )
    }

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

    override fun match(assignedType: Type): Type.MatchResult = when (assignedType) {
        this -> Type.TotalMatch
        else -> Type.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }

    override fun walkRecursive(): Sequence<Type> = emptySequence()

    override fun dump(): String = "#${formula.name.name}"
}

data class TypeVariableResolution(
    val resolvedTypeByVariable: Map<TypeVariable, Type>,
) {
    val resolvedTypeVariables: Set<TypeVariable>
        get() = resolvedTypeByVariable.keys

    fun mergeWith(
        other: TypeVariableResolution,
    ): TypeVariableResolution {
        // TODO: Check for resolution incompatibilities
        return TypeVariableResolution(
            resolvedTypeByVariable = resolvedTypeByVariable + other.resolvedTypeByVariable,
        )
    }

    fun withoutTypeVariables(
        typeVariables: Set<TypeVariable>,
    ) = TypeVariableResolution(
        resolvedTypeByVariable = resolvedTypeByVariable.filterKeys {
            !typeVariables.contains(it)
        },
    )

    companion object {
        val Empty = TypeVariableResolution(
            resolvedTypeByVariable = emptyMap(),
        )
    }
}

data class TypeVariableResolutionError(
    override val message: String,
) : Exception()
