package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

/**
 * A symbol for an illegal type, a result of a typing error.
 */
data object IllType : MembershipType() {
    override fun findLowestCommonSupertype(other: MembershipType): MembershipType = IllType

    override fun resolveTypeVariables(assignedType: MembershipType): TypeVariableResolution {
        // Note: This might need an improvement
        return TypeVariableResolution.Empty
    }

    override fun substituteTypeVariables(resolution: TypeVariableResolution): MembershipType = IllType
    override fun match(assignedType: MembershipType): MembershipType.MatchResult = MembershipType.TotalMatch

    override fun dumpDirectly(depth: Int): String = "IllType"

    override fun walkRecursive(): Sequence<MembershipType> = emptySequence()
}
