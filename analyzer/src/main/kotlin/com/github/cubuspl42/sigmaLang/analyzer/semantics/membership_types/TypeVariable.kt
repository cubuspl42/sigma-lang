package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula

data class TypeVariable(
    // TODO
    val formula: Formula,
) : MembershipType() {
    companion object {
        fun of(name: String) = TypeVariable(
            formula = Formula.of(name),
        )

        fun of(name: Identifier) = TypeVariable(
            formula = Formula(name = name),
        )
    }

    override fun findLowestCommonSupertype(
        other: MembershipType,
    ): MembershipType = AnyType

    override fun resolveTypeVariables(
        assignedType: MembershipType,
    ): TypeVariableResolution = TypeVariableResolution(
        resolvedTypeByVariable = mapOf(this to assignedType),
    )

    // Thought: Return an error if resolution misses this variable?
    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): MembershipType = resolution.resolvedTypeByVariable[this] ?: this

    override fun match(assignedType: MembershipType): MembershipType.MatchResult = when (assignedType) {
        this -> MembershipType.TotalMatch
        else -> MembershipType.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }

    override fun walkRecursive(): Sequence<MembershipType> = emptySequence()

    override fun dumpDirectly(depth: Int): String = "#${formula.name.name}"
}

data class TypeVariableResolution(
    val resolvedTypeByVariable: Map<TypeVariable, MembershipType>,
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
