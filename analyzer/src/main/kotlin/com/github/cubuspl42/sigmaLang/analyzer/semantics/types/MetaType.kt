package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

object MetaType : ShapeType() {
    override fun findLowestCommonSupertype(other: Type): Type = AnyType

    override fun resolveTypeVariables(assignedType: Type): TypeVariableResolution = TypeVariableResolution.Empty

    override fun substituteTypeVariables(resolution: TypeVariableResolution): Type = this

    override fun matchShape(assignedType: Type): Type.MatchResult = when (assignedType) {
        is MetaType -> Type.TotalMatch
        else -> Type.TotalMismatch(
            expectedType = MetaType,
            actualType = assignedType,
        )
    }

    override fun walkRecursive(): Sequence<Type> = emptySequence()

    override fun dump(): String = "(meta-type)"
}
