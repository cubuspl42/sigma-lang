package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

object NeverType : SpecificType() {
    data object AssignmentMismatch : SpecificType.Mismatch() {
        override fun dump(): String = "nothing can be assigned to Never"
    }

    override fun findLowestCommonSupertype(
        other: SpecificType,
    ): SpecificType = other

    override fun resolveTypePlaceholders(
        assignedType: SpecificType,
    ): TypePlaceholderResolution = TypePlaceholderResolution.Empty

    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> = TypePlaceholderSubstitution(
        result = IllType,
    )

    override fun match(
        assignedType: SpecificType,
    ): SpecificType.MatchResult = AssignmentMismatch

    override fun dumpDirectly(depth: Int): String = "Never"

    override fun walkRecursive(): Sequence<SpecificType> = emptySequence()
}
