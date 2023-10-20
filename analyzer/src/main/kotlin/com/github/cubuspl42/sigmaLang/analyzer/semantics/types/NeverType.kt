package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

object NeverType : MembershipType() {
    data object AssignmentMismatch : MembershipType.Mismatch() {
        override fun dump(): String = "nothing can be assigned to Never"
    }

    override fun findLowestCommonSupertype(
        other: MembershipType,
    ): MembershipType = other

    override fun resolveTypePlaceholders(
        assignedType: MembershipType,
    ): TypePlaceholderResolution = TypePlaceholderResolution.Empty

    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> = TypePlaceholderSubstitution(
        result = IllType,
    )

    override fun match(
        assignedType: MembershipType,
    ): MembershipType.MatchResult = AssignmentMismatch

    override fun dumpDirectly(depth: Int): String = "Never"

    override fun walkRecursive(): Sequence<MembershipType> = emptySequence()
}
