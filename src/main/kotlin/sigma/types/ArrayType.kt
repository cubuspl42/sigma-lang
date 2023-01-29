package sigma.types

import sigma.values.TypeError

data class ArrayType(
    val elementType: Type,
) : TableType() {
    override val keyType = IntCollectiveType

    override val valueType: Type = elementType

    override fun isDefinitelyEmpty(): Boolean = false

    override fun resolveTypeVariables(assignedType: Type): TypeVariableResolution {
        if (assignedType !is ArrayType) throw TypeVariableResolutionError(
            message = "Cannot resolve type variables, non-array is assigned",
        )

        return elementType.resolveTypeVariables(
            assignedType = assignedType.elementType,
        )
    }

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): Type = ArrayType(
        elementType = elementType.substituteTypeVariables(
            resolution = resolution,
        ),
    )

    override fun dump(): String = "[${elementType.dump()}*]"
}
