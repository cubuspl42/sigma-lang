package sigma.semantics.types

data class ArrayType(
    val elementType: Type,
) : TableType() {
    data class ArrayMatch(
        val elementMatch: Type.MatchResult,
    ) : Type.PartialMatch() {
        override fun isFull(): Boolean = elementMatch.isFull()
        override fun dump(): String = "Array element type:\n" + elementMatch.dump()
    }

    data class OrderedTupleMatch(
        val elementsMatches: List<Type.MatchResult>,
    ) : Type.PartialMatch() {
        companion object {
            fun dumpElementsMatches(
                elementsMatches: List<Type.MatchResult>,
            ): String {
                val firstMismatchIndexed = elementsMatches.withIndex().firstOrNull { !it.value.isFull() }

                return when {
                    firstMismatchIndexed != null -> {
                        val index = firstMismatchIndexed.index
                        val firstMismatch = firstMismatchIndexed.value

                        return "at index $index: " + firstMismatch.dump()
                    }

                    else -> "(?)"
                }
            }

        }

        override fun isFull(): Boolean = elementsMatches.all {
            it.isFull()
        }

        override fun dump(): String = dumpElementsMatches(
            elementsMatches = elementsMatches,
        )
    }

    override val asArray = this

    override val keyType = IntCollectiveType

    override val valueType: Type = elementType

    override fun isDefinitelyEmpty(): Boolean = false

    override fun resolveTypeVariables(assignedType: Type): TypeVariableResolution {
        val assignedArrayType = assignedType.asArray ?: throw TypeVariableResolutionError(
            message = "Cannot resolve type variables, non-array is assigned (${assignedType.dump()})",
        )

        return elementType.resolveTypeVariables(
            assignedType = assignedArrayType.elementType,
        )
    }

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): ArrayType = ArrayType(
        elementType = elementType.substituteTypeVariables(
            resolution = resolution,
        ),
    )

    override fun match(assignedType: Type): MatchResult = when (assignedType) {
        is ArrayType -> ArrayMatch(
            elementMatch = elementType.match(
                assignedType = assignedType.elementType,
            ),
        )

        is OrderedTupleType -> OrderedTupleMatch(
            elementsMatches = assignedType.elements.map {
                elementType.match(assignedType = it.type)
            },
        )

        else -> Type.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }

    override fun dump(): String = "[${elementType.dump()}*]"

    override fun walkRecursive(): Sequence<Type> = elementType.walk()
}
