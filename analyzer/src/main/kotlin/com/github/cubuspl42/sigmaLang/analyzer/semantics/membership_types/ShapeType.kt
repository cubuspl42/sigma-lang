package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

sealed class ShapeType : MembershipType() {
    override fun match(assignedType: MembershipType): MatchResult {
        if (assignedType is UnionType) {
            val nonMatchingTypes = assignedType.memberTypes.filterNot {
                matchShape(it).isFull()
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

    override fun resolveTypeVariables(
        assignedType: MembershipType,
    ): TypeVariableResolution = if (assignedType is UnionType) {
        TypeVariableResolution.Empty // TODO
    } else {
        resolveTypeVariablesShape(assignedType = assignedType)
    }

    abstract fun resolveTypeVariablesShape(
        assignedType: MembershipType,
    ): TypeVariableResolution
}
