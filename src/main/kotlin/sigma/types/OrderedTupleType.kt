package sigma.types

import sigma.values.IntValue
import sigma.values.PrimitiveValue
import sigma.values.Symbol

data class OrderedTupleType(
    val entries: List<Entry>,
) : TupleType() {
    data class Entry(
        val name: Symbol?,
        val valueType: Type,
    )

    companion object {
        val Empty = OrderedTupleType(
            entries = emptyList(),
        )
    }

    override fun isAssignableTo(otherType: Type): Boolean {
        TODO("Not yet implemented")
    }

    override fun dump(): String {
        val dumpedEntries = entries.map { (name, valueType) ->
            listOfNotNull(
                name?.let { "${it.name}: " }, valueType.dump()
            ).joinToString(
                separator = " ",
            )
        }

        return "[${dumpedEntries.joinToString(separator = ", ")}]"
    }

    override val keyType: PrimitiveType = IntCollectiveType

    override val valueType: Type
        get() = TODO("value1 | value2 | ...")

    override fun isDefinitelyEmpty(): Boolean = entries.isEmpty()

    override val valueTypeByKey: Map<PrimitiveValue, Type> = entries.withIndex().associate { (index, entry) ->
        IntValue(index) to entry.valueType
    }

    override val valueTypeByLabel: Map<Symbol, Type> = entries.mapNotNull { entry ->
        entry.name?.let { name -> name to entry.valueType }
    }.toMap()
}
