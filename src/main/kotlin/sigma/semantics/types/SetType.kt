package sigma.semantics.types

import sigma.evaluation.values.TypeErrorException

// Type of sets
data class SetType(
    val elementType: Type,
) : Type() {
    data class SetMatch(
        val elementMatch: Type.MatchResult,
    ) : Type.PartialMatch() {
        override fun isFull(): Boolean = elementMatch.isFull()
        override fun dump(): String = when {
            !elementMatch.isFull() -> "elementMatch: " + elementMatch.dump()
            else -> "(?)"
        }
    }

    override fun dump(): String = "{${elementType.dump()}*}"

    override fun findLowestCommonSupertype(
        other: Type,
    ): Type = when (other) {
        is SetType -> SetType(
            elementType = elementType.findLowestCommonSupertype(other.elementType),
        )

        else -> AnyType
    }

    override fun resolveTypeVariables(
        assignedType: Type,
    ): TypeVariableResolution {
        if (assignedType !is SetType) throw TypeErrorException(
            message = "Cannot resolve type variables, non-set is assigned",
        )

        val elementResolution = elementType.resolveTypeVariables(
            assignedType = assignedType.elementType,
        )

        return elementResolution
    }

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): SetType = SetType(
        elementType = elementType.substituteTypeVariables(
            resolution = resolution,
        ),
    )

    override fun match(
        assignedType: Type,
    ): MatchResult = when (assignedType) {
        is SetType -> SetMatch(
            elementMatch = assignedType.elementType.match(
                assignedType = elementType,
            ),
        )

        else -> Type.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }
}
