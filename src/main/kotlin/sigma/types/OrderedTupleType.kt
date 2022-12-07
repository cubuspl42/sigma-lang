package sigma.types

import sigma.StaticValueScope
import sigma.values.Symbol

data class OrderedTupleType(
    val elements: List<Element>,
) : TupleType() {
    data class Element(
        // Idea: "label"?
        val name: Symbol?,
        val type: Type,
    )

    companion object {
        val Empty = OrderedTupleType(
            elements = emptyList(),
        )
    }

    override fun isAssignableTo(otherType: Type): Boolean {
        TODO("Not yet implemented")
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

    override fun toStaticValueScope(): StaticValueScope = object : StaticValueScope {
        override fun getValueType(
            valueName: Symbol,
        ): Type? = elements.singleOrNull { entry ->
            valueName == entry.name
        }?.type
    }
}
