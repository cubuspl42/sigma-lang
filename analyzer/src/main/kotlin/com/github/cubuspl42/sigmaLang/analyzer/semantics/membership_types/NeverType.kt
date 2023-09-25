package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

object NeverType : MembershipType() {
    data object AssignmentMismatch : MembershipType.Mismatch() {
        override fun dump(): String = "nothing can be assigned to Never"
    }

    override fun findLowestCommonSupertype(
        other: MembershipType,
    ): MembershipType = other

    override fun resolveTypeVariables(
        assignedType: MembershipType,
    ): TypeVariableResolution = TypeVariableResolution.Empty

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): MembershipType {
        return this
    }

    override fun match(
        assignedType: MembershipType,
    ): MembershipType.MatchResult = AssignmentMismatch

    override fun dump(): String = "Never"

    override fun walkRecursive(): Sequence<MembershipType> = emptySequence()
}
