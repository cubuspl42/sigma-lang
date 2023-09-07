package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

sealed class PrimitiveType : ShapeType() {
    override fun resolveTypeVariablesShape(
        assignedType: Type,
    ): TypeVariableResolution = TypeVariableResolution.Empty

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): PrimitiveType = this

    override fun matchShape(
        assignedType: Type,
    ): Type.MatchResult = when (this.findLowestCommonSupertype(assignedType)) {
        this -> Type.TotalMatch
        else -> Type.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }

    final override fun walkRecursive(): Sequence<Type> = emptySequence()
}
