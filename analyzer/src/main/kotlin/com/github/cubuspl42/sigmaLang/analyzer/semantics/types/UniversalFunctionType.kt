package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TypeErrorException

data class UniversalFunctionType(
    override val argumentType: TypeAlike,
    override val imageType: TypeAlike,
) : FunctionType() {
    data class UniversalFunctionMatch(
        val argumentMatch: SpecificType.MatchResult,
        val imageMatch: SpecificType.MatchResult,
    ) : SpecificType.PartialMatch() {
        override fun isFull(): Boolean = argumentMatch.isFull() && imageMatch.isFull()

        override fun dump(): String = when {
            !argumentMatch.isFull() -> "argument: " + argumentMatch.dump()
            !imageMatch.isFull() -> "image: " + imageMatch.dump()
            else -> "(?)"
        }
    }

    override fun resolveTypePlaceholdersShape(
        assignedType: TypeAlike,
    ): TypePlaceholderResolution {
        if (assignedType !is UniversalFunctionType) throw TypeErrorException(
            message = "Cannot resolve type variables, non-abstraction is assigned",
        )

        val argumentResolution = argumentType.resolveTypePlaceholders(
            assignedType = assignedType.argumentType as SpecificType,
        )

        val imageResolution = imageType.resolveTypePlaceholders(
            assignedType = assignedType.imageType as SpecificType,
        )

        return argumentResolution.mergeWith(imageResolution)
    }

    override fun replaceTypeRecursively(
        context: TypeReplacementContext,
    ): TypeAlike = UniversalFunctionType(
        argumentType = argumentType.replaceTypeDirectly(context = context),
        imageType = imageType.replaceTypeDirectly(context = context),
    )

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
        assignedType: SpecificType,
    ): SpecificType.MatchResult = when (assignedType) {
        is UniversalFunctionType -> UniversalFunctionMatch(
            argumentMatch = assignedType.argumentType.match(
                assignedType = argumentType as SpecificType,
            ),
            imageMatch = imageType.match(
                assignedType = assignedType.imageType as SpecificType,
            ),
        )

        else -> SpecificType.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }

    override fun walkRecursive(): Sequence<SpecificType> =
        (argumentType as SpecificType).walk() + (imageType as SpecificType).walk()

    override fun dumpDirectly(depth: Int): String = listOfNotNull(
        "${argumentType.dumpRecursively(depth = depth + 1)} -> ${imageType.dumpRecursively(depth = depth + 1)}",
    ).joinToString(separator = " ")

    override fun isNonEquivalentToDirectly(innerContext: NonEquivalenceContext, otherType: SpecificType): Boolean {
        if (otherType !is UniversalFunctionType) return true

        return (argumentType as SpecificType).isNonEquivalentToRecursively(
            outerContext = innerContext,
            otherType = otherType.argumentType as SpecificType,
        ) || (imageType as SpecificType).isNonEquivalentToRecursively(
            outerContext = innerContext,
            otherType = otherType.imageType as SpecificType,
        )
    }


}
