package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

sealed class FunctionType : ShapeType() {
    // This can be improved
    override fun findLowestCommonSupertype(
        other: MembershipType,
    ): MembershipType = AnyType

    abstract override fun substituteTypeVariables(resolution: TypeVariableResolution): FunctionType

    open val genericParameters: Set<TypeVariable> = emptySet()

    abstract val argumentType: MembershipType

    abstract val imageType: MembershipType
}
