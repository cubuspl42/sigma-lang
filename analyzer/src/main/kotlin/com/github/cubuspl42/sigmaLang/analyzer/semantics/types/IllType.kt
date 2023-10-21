package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

/**
 * A symbol for an illegal type, a result of a typing error.
 */
data object IllType : SpecificType() {
    override fun findLowestCommonSupertype(other: SpecificType): SpecificType = IllType

    override fun resolveTypePlaceholders(assignedType: SpecificType): TypePlaceholderResolution {
        // Note: This might need an improvement
        return TypePlaceholderResolution.Empty
    }

    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> = TypePlaceholderSubstitution(
        result = IllType,
    )

    override fun match(assignedType: SpecificType): SpecificType.MatchResult = SpecificType.TotalMatch

    override fun dumpDirectly(depth: Int): String = "IllType"

    override fun walkRecursive(): Sequence<SpecificType> = emptySequence()
}
