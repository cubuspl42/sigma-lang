package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TypeErrorException

data class UniversalFunctionType(
    override val argumentType: TypeAlike,
    override val imageType: TypeAlike,
) : FunctionType() {
    data class UniversalFunctionMatch(
        val argumentMatch: MembershipType.MatchResult,
        val imageMatch: MembershipType.MatchResult,
    ) : MembershipType.PartialMatch() {
        override fun isFull(): Boolean = argumentMatch.isFull() && imageMatch.isFull()

        override fun dump(): String = when {
            !argumentMatch.isFull() -> "argument: " + argumentMatch.dump()
            !imageMatch.isFull() -> "image: " + imageMatch.dump()
            else -> "(?)"
        }
    }

    override fun resolveTypeVariablesShape(
        assignedType: TypeAlike,
    ): TypePlaceholderResolution {
        if (assignedType !is UniversalFunctionType) throw TypeErrorException(
            message = "Cannot resolve type variables, non-abstraction is assigned",
        )

        val argumentResolution = argumentType.resolveTypePlaceholders(
            assignedType = assignedType.argumentType as MembershipType,
        )

        val imageResolution = imageType.resolveTypePlaceholders(
            assignedType = assignedType.imageType as MembershipType,
        )

        return argumentResolution.mergeWith(imageResolution)
    }

    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> = TypePlaceholderSubstitution.combine2(
        substitutionA = argumentType.substituteTypePlaceholders(
            resolution = resolution,
        ),
        substitutionB = imageType.substituteTypePlaceholders(
            resolution = resolution,
        ),
    ) { substitutedArgumentType, substitutedImageType ->
        UniversalFunctionType(
            argumentType = substitutedArgumentType,
            imageType = substitutedImageType,
        )
    }

    override fun matchShape(
        assignedType: MembershipType,
    ): MembershipType.MatchResult = when (assignedType) {
        is UniversalFunctionType -> UniversalFunctionMatch(
            argumentMatch = assignedType.argumentType.match(
                assignedType = argumentType as MembershipType,
            ),
            imageMatch = imageType.match(
                assignedType = assignedType.imageType as MembershipType,
            ),
        )

        else -> MembershipType.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }

    override fun walkRecursive(): Sequence<MembershipType> =
        (argumentType as MembershipType).walk() + (imageType as MembershipType).walk()

    override fun dumpDirectly(depth: Int): String = listOfNotNull(
        "${argumentType.dumpRecursively(depth = depth + 1)} -> ${imageType.dumpRecursively(depth = depth + 1)}",
    ).joinToString(separator = " ")

    override fun isNonEquivalentToDirectly(innerContext: NonEquivalenceContext, otherType: MembershipType): Boolean {
        if (otherType !is UniversalFunctionType) return true

        return (argumentType as MembershipType).isNonEquivalentToRecursively(
            outerContext = innerContext,
            otherType = otherType.argumentType as MembershipType,
        ) || (imageType as MembershipType).isNonEquivalentToRecursively(
            outerContext = innerContext,
            otherType = otherType.imageType as MembershipType,
        )
    }
}
