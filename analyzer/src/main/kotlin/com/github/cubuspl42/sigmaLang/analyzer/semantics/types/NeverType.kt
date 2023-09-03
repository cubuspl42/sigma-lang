package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

object NeverType : SealedType() {
    data object AssignmentMismatch : Type.Mismatch() {
        override fun dump(): String = "nothing can be assigned to Never"
    }

    override fun findLowestCommonSupertype(
        other: Type,
    ): Type = other

    override fun resolveTypeVariables(
        assignedType: Type,
    ): TypeVariableResolution = TypeVariableResolution.Empty

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): Type {
        return this
    }

    override fun match(
        assignedType: Type,
    ): Type.MatchResult = AssignmentMismatch

    override fun dump(): String = "Never"

    override fun walkRecursive(): Sequence<Type> = emptySequence()
}
