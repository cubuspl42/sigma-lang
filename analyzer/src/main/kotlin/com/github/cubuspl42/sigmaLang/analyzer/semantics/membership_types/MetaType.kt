package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

object MetaType : ShapeType() {
    override fun findLowestCommonSupertype(other: MembershipType): MembershipType = AnyType

    override fun resolveTypeVariablesShape(assignedType: MembershipType): TypeVariableResolution = TypeVariableResolution.Empty

    override fun substituteTypeVariables(resolution: TypeVariableResolution): MembershipType = this

    override fun matchShape(assignedType: MembershipType): MembershipType.MatchResult = when (assignedType) {
        is MetaType -> MembershipType.TotalMatch
        else -> MembershipType.TotalMismatch(
            expectedType = MetaType,
            actualType = assignedType,
        )
    }

    override fun walkRecursive(): Sequence<MembershipType> = emptySequence()

    override fun dump(): String = "(meta-type)"
}
