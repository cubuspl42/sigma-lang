package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

object AnyType : MembershipType() {
    override fun findLowestCommonSupertype(
        other: MembershipType,
    ): MembershipType = AnyType

    override fun resolveTypePlaceholders(
        assignedType: MembershipType,
    ): TypePlaceholderResolution = TypePlaceholderResolution.Empty

    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> = TypePlaceholderSubstitution(
        result = this,
    )

    override fun match(
        assignedType: MembershipType,
    ): MembershipType.MatchResult = MembershipType.TotalMatch

    override fun dumpDirectly(depth: Int): String = "Any"

    override fun walkRecursive(): Sequence<MembershipType> = emptySequence()
}
