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
        assignedType: SpecificType,
    ): SpecificType.MatchResult = when (this.findLowestCommonSupertype(assignedType)) {
        this -> SpecificType.TotalMatch
        else -> SpecificType.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }

    final override fun walkRecursive(): Sequence<SpecificType> = emptySequence()

    override fun replaceTypeRecursively(context: TypeReplacementContext): TypeAlike = this
}
