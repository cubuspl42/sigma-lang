package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

sealed class PrimitiveType : ShapeType() {
    override fun resolveTypeVariablesShape(
        assignedType: MembershipType,
    ): TypeVariableResolution = TypeVariableResolution.Empty

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): PrimitiveType = this

    override fun matchShape(
        assignedType: MembershipType,
    ): MembershipType.MatchResult = when (this.findLowestCommonSupertype(assignedType)) {
        this -> MembershipType.TotalMatch
        else -> MembershipType.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }

    final override fun walkRecursive(): Sequence<MembershipType> = emptySequence()
}
