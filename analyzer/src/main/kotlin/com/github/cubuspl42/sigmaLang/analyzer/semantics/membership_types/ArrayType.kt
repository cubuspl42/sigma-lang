package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

data class ArrayType(
    val elementType: TypeAlike,
) : TableType() {
    data class ArrayMatch(
        val elementMatch: MembershipType.MatchResult,
    ) : MembershipType.PartialMatch() {
        override fun isFull(): Boolean = elementMatch.isFull()
        override fun dump(): String = "Array element type:\n" + elementMatch.dump()
    }

    data class OrderedTupleMatch(
        val elementsMatches: List<MembershipType.MatchResult>,
    ) : MembershipType.PartialMatch() {
        companion object {
            fun dumpElementsMatches(
                elementsMatches: List<MembershipType.MatchResult>,
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

    override fun resolveTypeVariablesShape(assignedType: TypeAlike): TypePlaceholderResolution {
        val assignedArrayType = assignedType.asArray ?: throw TypeVariableResolutionError(
            message = "Cannot resolve type variables, non-array is assigned (${assignedType.dump()})",
        )

        return elementType.resolveTypePlaceholders(
            assignedType = assignedArrayType.elementType as MembershipType,
        )
    }

    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> = elementType.substituteTypePlaceholders(
        resolution = resolution,
    ).transform {
        ArrayType(
            elementType = it,
        )
    }

    override fun matchShape(assignedType: MembershipType): MembershipType.MatchResult =
        when (assignedType) {
            is ArrayType -> ArrayMatch(
                elementMatch = elementType.match(
                    assignedType = assignedType.elementType as MembershipType,
                ),
            )

            is OrderedTupleType -> OrderedTupleMatch(
                elementsMatches = assignedType.elements.map {
                    elementType.match(assignedType = it.type as MembershipType)
                },
            )

            else -> MembershipType.TotalMismatch(
                expectedType = this,
                actualType = assignedType,
            )
        }

    override fun dumpDirectly(depth: Int): String = "[${elementType.dumpRecursively(depth = depth + 1)}*]"

    override fun walkRecursive(): Sequence<MembershipType> = (elementType as MembershipType).walk()
}
