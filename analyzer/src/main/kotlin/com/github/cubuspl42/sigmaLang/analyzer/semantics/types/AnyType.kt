package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

object AnyType : SealedType() {
    override fun findLowestCommonSupertype(
        other: Type,
    ): Type = AnyType

    override fun resolveTypeVariables(
        assignedType: Type,
    ): TypeVariableResolution = TypeVariableResolution.Empty

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): AnyType = this

    override fun match(
        assignedType: Type,
    ): Type.MatchResult = Type.TotalMatch

    override fun dump(): String = "Any"

    override fun walkRecursive(): Sequence<Type> = emptySequence()
}
