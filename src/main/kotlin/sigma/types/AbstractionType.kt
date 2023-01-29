package sigma.types

import sigma.values.TypeError

sealed class FunctionType : Type() {
    abstract val argumentType: Type
    abstract val imageType: Type
}

data class AbstractionType(
    override val argumentType: TupleType,
    override val imageType: Type,
) : FunctionType() {
    override fun resolveTypeVariables(
        assignedType: Type,
    ): TypeVariableResolution {
        if (assignedType !is AbstractionType) throw TypeError(
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
    ): AbstractionType = AbstractionType(
        argumentType = argumentType.substituteTypeVariables(
            resolution = resolution,
        ),
        imageType = imageType.substituteTypeVariables(
            resolution  = resolution,
        ),
    )

    override fun dump() = "${argumentType.dump()} -> ${imageType.dump()}"
}
