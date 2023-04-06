package sigma.semantics.types

import sigma.values.TypeError

// Type of tables with keys of a single primitive type and values of a single
// specific type
data class DictType(
    override val keyType: PrimitiveType,
    override val valueType: Type,
) : TableType() {

    override fun dump(): String = "{${keyType.dump()} ~> ${valueType.dump()}}"

    override fun isDefinitelyEmpty(): Boolean = false

    override fun resolveTypeVariables(
        assignedType: Type,
    ): TypeVariableResolution {
        if (assignedType !is DictType) throw TypeError(
            message = "Cannot resolve type variables, non-dict is assigned",
        )

        val keyResolution = keyType.resolveTypeVariables(
            assignedType = assignedType.keyType,
        )

        val valueResolution = valueType.resolveTypeVariables(
            assignedType = assignedType.valueType,
        )

        return keyResolution.mergeWith(valueResolution)
    }

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): Type = DictType(
        keyType = keyType,
        valueType = valueType.substituteTypeVariables(
            resolution = resolution,
        ),
    )
}
