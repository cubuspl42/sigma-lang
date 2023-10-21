package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

/// Type of types
object TypeType : ShapeType() {
    override fun findLowestCommonSupertype(other: SpecificType): SpecificType = AnyType

    override fun resolveTypeVariablesShape(assignedType: TypeAlike): TypePlaceholderResolution =
        TypePlaceholderResolution.Empty

    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> = TypePlaceholderSubstitution(
        result = this,
    )

    override fun matchShape(assignedType: SpecificType): SpecificType.MatchResult = when (assignedType) {
        is TypeType -> SpecificType.TotalMatch
        else -> SpecificType.TotalMismatch(
            expectedType = TypeType,
            actualType = assignedType,
        )
    }

    override fun walkRecursive(): Sequence<SpecificType> = emptySequence()

    override fun dumpDirectly(depth: Int): String = "Type"
}
