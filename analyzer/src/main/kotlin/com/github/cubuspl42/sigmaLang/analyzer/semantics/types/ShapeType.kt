package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

sealed class ShapeType : MembershipType() {
    override fun match(assignedType: MembershipType): MatchResult {
        if (assignedType is UnionType) {
            val nonMatchingTypes = assignedType.memberTypes.mapNotNull { memberType ->
                (memberType as MembershipType).takeIf { !matchShape(assignedType = it).isFull() }
            }.toSet()

            return if (nonMatchingTypes.isNotEmpty()) {
                UnionType.AssignedUnionMatch(
                    expectedType = this,
                    nonMatchingTypes = nonMatchingTypes,
                )
            } else {
                TotalMatch
            }
        } else {
            return matchShape(assignedType = assignedType)
        }
    }

    abstract fun matchShape(
        assignedType: MembershipType,
    ): MatchResult

    override fun resolveTypePlaceholders(
        assignedType: MembershipType,
    ): TypePlaceholderResolution = if (assignedType is UnionType) {
        TypePlaceholderResolution.Empty // TODO
    } else {
        resolveTypeVariablesShape(assignedType = assignedType)
    }

    abstract fun resolveTypeVariablesShape(
        assignedType: TypeAlike,
    ): TypePlaceholderResolution
}
