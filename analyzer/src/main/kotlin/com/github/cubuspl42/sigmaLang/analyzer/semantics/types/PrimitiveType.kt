package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

sealed class PrimitiveType : ShapeType() {
    override fun resolveTypeVariablesShape(
        assignedType: TypeAlike,
    ): TypePlaceholderResolution = TypePlaceholderResolution.Empty

    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> = TypePlaceholderSubstitution(
        result = this,
    )

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
