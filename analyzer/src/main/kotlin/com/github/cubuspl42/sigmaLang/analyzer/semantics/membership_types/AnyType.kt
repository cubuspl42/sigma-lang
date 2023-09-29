package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

object AnyType : MembershipType() {
    override fun findLowestCommonSupertype(
        other: MembershipType,
    ): MembershipType = AnyType

    override fun resolveTypeVariables(
        assignedType: MembershipType,
    ): TypeVariableResolution = TypeVariableResolution.Empty

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): AnyType = this

    override fun match(
        assignedType: MembershipType,
    ): MembershipType.MatchResult = MembershipType.TotalMatch

    override fun dumpDirectly(depth: Int): String = "Any"

    override fun walkRecursive(): Sequence<MembershipType> = emptySequence()
}
