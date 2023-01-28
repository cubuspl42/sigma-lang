package sigma.types

import sigma.StaticValueScope
import sigma.values.Symbol

// Type of tables with fixed number of entries, with keys being symbols, and any
// values
data class UnorderedTupleType(
    val valueTypeByName: Map<Symbol, Type>,
) : TupleType() {
    companion object {
        val Empty = UnorderedTupleType(
            valueTypeByName = emptyMap(),
        )
    }

    override fun dump(): String {
        val dumpedEntries = valueTypeByName.map { (name, valueType) ->
            "(${name.dump()}): ${valueType.dump()}"
        }

        return "{${dumpedEntries.joinToString()}}"
    }

    override val keyType: PrimitiveType
        get() = TODO("key1 | key2 | ...")

    override val valueType: Type
        get() = TODO("value1 | value2 | ...")

    override fun isDefinitelyEmpty(): Boolean = valueTypeByName.isEmpty()

    override fun toStaticValueScope(): StaticValueScope = object : StaticValueScope {
        override fun getValueType(valueName: Symbol): Type? = valueTypeByName[valueName]
    }
}
