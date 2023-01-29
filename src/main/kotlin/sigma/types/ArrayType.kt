package sigma.types

data class ArrayType(
    val elementType: Type,
) : TableType() {
    override val asArray = this

    override val keyType = IntCollectiveType

    override val valueType: Type = elementType

    override fun isDefinitelyEmpty(): Boolean = false

    override fun resolveTypeVariables(assignedType: Type): TypeVariableResolution {
        val assignedArrayType = assignedType.asArray ?: throw TypeVariableResolutionError(
            message = "Cannot resolve type variables, non-array is assigned",
        )

        return elementType.resolveTypeVariables(
            assignedType = assignedArrayType.elementType,
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
