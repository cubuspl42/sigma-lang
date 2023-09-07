package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

sealed class ShapeType : Type() {
    override fun match(assignedType: Type): MatchResult {
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
        assignedType: Type,
    ): MatchResult
}
