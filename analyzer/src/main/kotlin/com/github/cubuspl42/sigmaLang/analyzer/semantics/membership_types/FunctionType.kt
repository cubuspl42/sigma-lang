package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

sealed class FunctionType : ShapeType() {
    // This can be improved
    override fun findLowestCommonSupertype(
        other: MembershipType,
    ): MembershipType = AnyType

    abstract override fun substituteTypePlaceholders(resolution: TypePlaceholderResolution): TypePlaceholderSubstitution<TypeAlike>

    abstract val argumentType: TypeAlike

    abstract val imageType: TypeAlike
}
