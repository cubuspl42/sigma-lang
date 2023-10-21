package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

sealed class FunctionType : ShapeType() {
    // This can be improved
    override fun findLowestCommonSupertype(
        other: SpecificType,
    ): SpecificType = AnyType

    abstract override fun substituteTypePlaceholders(resolution: TypePlaceholderResolution): TypePlaceholderSubstitution<TypeAlike>

    abstract val argumentType: TypeAlike

    abstract val imageType: TypeAlike
}
