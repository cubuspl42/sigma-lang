package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TypeErrorException

data class UniversalFunctionType(
    override val metaArgumentType: TupleType? = null,
    override val argumentType: TupleType,
    override val imageType: MembershipType,
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
        assignedType: MembershipType,
    ): TypeVariableResolution {
        if (assignedType !is UniversalFunctionType) throw TypeErrorException(
            message = "Cannot resolve type variables, non-abstraction is assigned",
        )

        val argumentResolution = argumentType.resolveTypeVariables(
            assignedType = assignedType.argumentType,
        )

        val imageResolution = imageType.resolveTypeVariables(
            assignedType = assignedType.imageType,
        )

        return argumentResolution.mergeWith(imageResolution).withoutTypeVariables(
            typeVariables = typeVariables,
        )
    }

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): UniversalFunctionType {
        val innerResolution = resolution.withoutTypeVariables(
            typeVariables = typeVariables,
        )

        return UniversalFunctionType(
            metaArgumentType = metaArgumentType,
            argumentType = argumentType.substituteTypeVariables(
                resolution = innerResolution,
            ),
            imageType = imageType.substituteTypeVariables(
                resolution = innerResolution,
            ),
        )
    }

    override fun matchShape(
        assignedType: MembershipType,
    ): MembershipType.MatchResult = when (assignedType) {
        is UniversalFunctionType -> UniversalFunctionMatch(
            argumentMatch = assignedType.argumentType.match(
                assignedType = argumentType,
            ),
            imageMatch = imageType.match(
                assignedType = assignedType.imageType,
            ),
        )

        else -> MembershipType.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }

    override fun walkRecursive(): Sequence<MembershipType> = argumentType.walk() + imageType.walk()

    override fun dumpDirectly(depth: Int): String = listOfNotNull(
        metaArgumentType?.dumpRecursively(depth = depth + 1),
        "${argumentType.dumpRecursively(depth = depth + 1)} -> ${imageType.dumpRecursively(depth = depth + 1)}",
    ).joinToString(separator = " ")

    override fun isNonEquivalentToDirectly(innerContext: NonEquivalenceContext, otherType: MembershipType): Boolean {
        if (otherType !is UniversalFunctionType) return true

        return argumentType.isNonEquivalentToRecursively(
            outerContext = innerContext,
            otherType = otherType.argumentType,
        ) || imageType.isNonEquivalentToRecursively(
            outerContext = innerContext,
            otherType = otherType.imageType,
        )
    }
}
