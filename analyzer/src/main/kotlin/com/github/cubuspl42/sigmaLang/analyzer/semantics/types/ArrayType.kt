package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

data class ArrayType(
    val elementType: TypeAlike,
) : TableType() {
    data class ArrayMatch(
        val elementMatch: SpecificType.MatchResult,
    ) : SpecificType.PartialMatch() {
        override fun isFull(): Boolean = elementMatch.isFull()
        override fun dump(): String = "Array element type:\n" + elementMatch.dump()
    }

    data class OrderedTupleMatch(
        val elementsMatches: List<SpecificType.MatchResult>,
    ) : SpecificType.PartialMatch() {
        companion object {
            fun dumpElementsMatches(
                elementsMatches: List<SpecificType.MatchResult>,
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

    override val valueType: TypeAlike = elementType

    override fun isDefinitelyEmpty(): Boolean = false

    override fun resolveTypePlaceholdersShape(assignedType: TypeAlike): TypePlaceholderResolution {
        val assignedArrayType = assignedType.asArray ?: throw TypeVariableResolutionError(
            message = "Cannot resolve type variables, non-array is assigned (${assignedType.dump()})",
        )

        return elementType.resolveTypePlaceholders(
            assignedType = assignedArrayType.elementType as SpecificType,
        )
    }

    override fun replaceTypeRecursively(context: TypeReplacementContext): TypeAlike = ArrayType(
        elementType = elementType.replaceTypeDirectly(context = context),
    )

    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> = elementType.substituteTypePlaceholders(
        resolution = resolution,
    ).transform {
        ArrayType(
            elementType = it,
        )
    }

    override fun matchShape(assignedType: SpecificType): SpecificType.MatchResult =
        when (assignedType) {
            is ArrayType -> ArrayMatch(
                elementMatch = elementType.match(
                    assignedType = assignedType.elementType as SpecificType,
                ),
            )

            is OrderedTupleType -> OrderedTupleMatch(
                elementsMatches = assignedType.indexedElements.map {
                    elementType.match(assignedType = it.type as SpecificType)
                },
            )

            else -> SpecificType.TotalMismatch(
                expectedType = this,
                actualType = assignedType,
            )
        }

    override fun dumpDirectly(depth: Int): String = "[${elementType.dumpRecursively(depth = depth + 1)}*]"

    override fun walkRecursive(): Sequence<SpecificType> = (elementType as SpecificType).walk()
}
