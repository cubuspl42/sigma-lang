package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

sealed class ShapeType : SpecificType() {
    override fun match(assignedType: SpecificType): MatchResult {
        if (assignedType is UnionType) {
            val nonMatchingTypes = assignedType.memberTypes.mapNotNull { memberType ->
                (memberType as SpecificType).takeIf { !matchShape(assignedType = it).isFull() }
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
        assignedType: SpecificType,
    ): MatchResult

    override fun resolveTypePlaceholders(
        assignedType: SpecificType,
    ): TypePlaceholderResolution = if (assignedType is UnionType) {
        TypePlaceholderResolution.Empty // TODO
    } else {
        resolveTypePlaceholdersShape(assignedType = assignedType)
    }

    abstract fun resolveTypePlaceholdersShape(
        assignedType: TypeAlike,
    ): TypePlaceholderResolution

    abstract override fun replaceTypeRecursively(context: TypeReplacementContext): TypeAlike
}
