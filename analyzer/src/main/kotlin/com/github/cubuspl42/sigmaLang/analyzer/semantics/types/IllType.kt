package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

/**
 * A symbol for an illegal type, a result of a typing error.
 */
object IllType : Type() {
    override fun findLowestCommonSupertype(other: Type): Type = IllType

    override fun resolveTypeVariables(assignedType: Type): TypeVariableResolution {
        // Note: This might need an improvement
        return TypeVariableResolution.Empty
    }

    override fun substituteTypeVariables(resolution: TypeVariableResolution): Type = IllType
    override fun match(assignedType: Type): Type.MatchResult = Type.TotalMatch

    override fun dump(): String = "IllType"

    override fun walkRecursive(): Sequence<Type> = emptySequence()
}
