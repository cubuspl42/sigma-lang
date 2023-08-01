package sigma.semantics.types

import sigma.evaluation.values.TypeErrorException

data class UniversalFunctionType(
    override val argumentType: TupleType,
    override val imageType: Type,
) : FunctionType() {
    data class UniversalFunctionMatch(
        val argumentMatch: Type.MatchResult,
        val imageMatch: Type.MatchResult,
    ) : PartialMatch() {
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

        return argumentResolution.mergeWith(imageResolution)
    }

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): UniversalFunctionType = UniversalFunctionType(
        argumentType = argumentType.substituteTypeVariables(
            resolution = resolution,
        ),
        imageType = imageType.substituteTypeVariables(
            resolution = resolution,
        ),
    )

    override fun match(
        assignedType: Type,
    ): MatchResult = when (assignedType) {
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

    override fun dump() = "${argumentType.dump()} -> ${imageType.dump()}"
}
