package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.*

// Type of sets
data class SetType(
    val elementType: TypeAlike,
) : ShapeType() {
    data class SetMatch(
        val elementMatch: SpecificType.MatchResult,
    ) : SpecificType.PartialMatch() {
        override fun isFull(): Boolean = elementMatch.isFull()
        override fun dump(): String = when {
            !elementMatch.isFull() -> "set element type: " + elementMatch.dump()
            else -> "(?)"
        }
    }

    override fun dumpDirectly(depth: Int): String = "{${elementType.dumpRecursively(depth = depth + 1)}*}"

    override fun findLowestCommonSupertype(
        other: SpecificType,
    ): SpecificType = when (other) {
        is SetType -> SetType(
            elementType = (elementType as SpecificType).findLowestCommonSupertype(other.elementType as SpecificType),
        )

        else -> AnyType
    }

    override fun resolveTypePlaceholdersShape(
        assignedType: TypeAlike,
    ): TypePlaceholderResolution {
        if (assignedType !is SetType) throw TypeErrorException(
            message = "Cannot resolve type variables, non-set is assigned",
        )

        val elementResolution = elementType.resolveTypePlaceholders(
            assignedType = assignedType.elementType as SpecificType,
        )

        return elementResolution
    }

    override fun replaceTypeRecursively(context: TypeReplacementContext): TypeAlike = SetType(
        elementType = elementType.replaceTypeDirectly(context = context),
    )

    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> =
        elementType.substituteTypePlaceholders(
            resolution = resolution,
        ).transform {
            SetType(
                elementType = it,
            )
        }

    override fun matchShape(
        assignedType: SpecificType,
    ): SpecificType.MatchResult = when (assignedType) {
        is SetType -> SetMatch(
            elementMatch = elementType.match(
                assignedType = assignedType.elementType as SpecificType,
            ),
        )

        else -> SpecificType.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }

    override fun walkRecursive(): Sequence<SpecificType> = (elementType as SpecificType).walk()
}
