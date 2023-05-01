package sigma.semantics.types

import sigma.values.TypeErrorException

data class UniversalFunctionType(
    override val argumentType: TupleType,
    override val imageType: Type,
) : FunctionType() {
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
            resolution  = resolution,
        ),
    )

    override fun dump() = "${argumentType.dump()} -> ${imageType.dump()}"
}
