package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

object AnyType : SpecificType() {
    override fun findLowestCommonSupertype(
        other: SpecificType,
    ): SpecificType = AnyType

    override fun resolveTypePlaceholders(
        assignedType: SpecificType,
    ): TypePlaceholderResolution = TypePlaceholderResolution.Empty

    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> = TypePlaceholderSubstitution(
        result = this,
    )

    override fun match(
        assignedType: SpecificType,
    ): SpecificType.MatchResult = SpecificType.TotalMatch

    override fun dumpDirectly(depth: Int): String = "Any"

    override fun walkRecursive(): Sequence<SpecificType> = emptySequence()
}
