package sigma.types

import sigma.StaticValueScope
import sigma.values.Symbol
import sigma.values.TypeError

data class OrderedTupleType(
    val elements: List<Element>,
) : TupleType() {
    data class Element(
        // Idea: "label"?
        val name: Symbol?,
        val type: Type,
    ) {
        fun substituteTypeVariables(
            resolution: TypeVariableResolution,
        ): Element = copy(
            type = type.substituteTypeVariables(
                resolution = resolution,
            )
        )
    }

    companion object {
        val Empty = OrderedTupleType(
            elements = emptyList(),
        )
    }

    override fun dump(): String {
        val dumpedEntries = elements.map { (name, type) ->
            listOfNotNull(
                name?.let { "${it.name}: " }, type.dump()
            ).joinToString(
                separator = " ",
            )
        }

        return "[${dumpedEntries.joinToString(separator = ", ")}]"
    }

    override val keyType: PrimitiveType = IntCollectiveType

    override val valueType: Type
        get() = TODO("value1 | value2 | ...")

    override fun isDefinitelyEmpty(): Boolean = elements.isEmpty()

    override fun resolveTypeVariables(
        assignedType: Type,
    ): TypeVariableResolution {
        if (assignedType !is OrderedTupleType) throw TypeError(
            message = "Cannot resolve type variables, non-(ordered tuple) is assigned",
        )

        return elements.withIndex().fold(
            initial = TypeVariableResolution.Empty,
        ) { accumulatedResolution, (index, element) ->
            val assignedElement = assignedType.elements.getOrNull(index) ?: throw TypeError(
                message = "Cannot resolve type variables, assigned tuple is shorter",
            )

            val elementResolution = element.type.resolveTypeVariables(
                assignedType = assignedElement.type,
            )

            accumulatedResolution.mergeWith(elementResolution)
        }
    }

    override fun toStaticValueScope(): StaticValueScope = object : StaticValueScope {
        override fun getValueType(
            valueName: Symbol,
        ): Type? = elements.singleOrNull { entry ->
            valueName == entry.name
        }?.type
    }

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): OrderedTupleType = OrderedTupleType(
        elements = elements.map {
            it.substituteTypeVariables(resolution = resolution)
        },
    )
}
