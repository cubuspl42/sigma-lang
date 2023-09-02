package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TypeErrorException

data class UniversalFunctionType(
    override val genericParameters: Set<TypeVariable> = emptySet(),
    override val argumentType: TupleType,
    override val imageType: Type,
) : FunctionType() {
    data class UniversalFunctionMatch(
        val argumentMatch: Type.MatchResult,
        val imageMatch: Type.MatchResult,
    ) : Type.PartialMatch() {
        override fun isFull(): Boolean = argumentMatch.isFull() && imageMatch.isFull()

        override fun dump(): String = when {
            !argumentMatch.isFull() -> "argument: " + argumentMatch.dump()
            !imageMatch.isFull() -> "image: " + imageMatch.dump()
            else -> "(?)"
        }
    }

    override fun resolveTypeVariables(
        assignedType: Type,
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
            typeVariables = genericParameters,
        )
    }

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): UniversalFunctionType {
        val innerResolution = resolution.withoutTypeVariables(
            typeVariables = genericParameters,
        )

        return UniversalFunctionType(
            genericParameters = genericParameters,
            argumentType = argumentType.substituteTypeVariables(
                resolution = innerResolution,
            ),
            imageType = imageType.substituteTypeVariables(
                resolution = innerResolution,
            ),
        )
    }

    override fun match(
        assignedType: Type,
    ): Type.MatchResult = when (assignedType) {
        is UniversalFunctionType -> UniversalFunctionMatch(
            argumentMatch = assignedType.argumentType.match(
                assignedType = argumentType,
            ),
            imageMatch = imageType.match(
                assignedType = assignedType.imageType,
            ),
        )

        else -> Type.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }

    override fun walkRecursive(): Sequence<Type> = argumentType.walk() + imageType.walk()

    override fun dump(): String = listOfNotNull(
        if (genericParameters.isNotEmpty()) "!{${genericParameters.joinToString(separator = ", ")}}" else null,
        "${argumentType.dump()} -> ${imageType.dump()}",
    ).joinToString(separator = " ")
}
