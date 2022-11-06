package sigma.types

import sigma.values.PrimitiveValue
import sigma.values.Symbol

sealed class TableType : FunctionType() {
    object Empty : TableType() {
        override val keyType: PrimitiveType = NeverType

        override val valueType: Type = NeverType

        override fun isDefinitelyEmpty(): Boolean = true

        override fun isAssignableTo(otherType: Type): Boolean {
            TODO("Not yet implemented")
        }

        override fun dump(): String = "{}"
    }

    final override val metaArgumentType: TableType = Empty

    final override val argumentType: Type
        get() = keyType

    final override val imageType: Type
        get() = TODO("valueType | UndefinedType")

    abstract val keyType: PrimitiveType

    /**
     * Any type but [UndefinedType]
     */
    abstract val valueType: Type

    abstract fun isDefinitelyEmpty(): Boolean
}

// TODO: Simplify this, at least for now. Add union types and iterate
// Struct and dict types should be enough
//
//interface StructType {
//    val literalEntries: Map<PrimitiveValue, Type>
//}
//
//interface LabeledTableType {
//    val labeledEntries: Map<Symbol, Type>
//}

abstract class TupleType : TableType() {
    abstract val valueTypeByKey: Map<PrimitiveValue, Type>

    abstract val valueTypeByLabel: Map<Symbol, Type>
}

// Type of tables with fixed number of entries, with keys of value types, and
// values of respective types
// is sometimes dict (if values are consistent)
// is sometimes array (if keys are consecutive)
data class UnorderedTupleType(
    override val valueTypeByKey: Map<PrimitiveValue, Type>,
) : TupleType() {
    companion object {
        val Empty = UnorderedTupleType(
            valueTypeByKey = emptyMap(),
        )
    }

    override fun isAssignableTo(otherType: Type): Boolean {
        TODO("Not yet implemented")
    }

    override fun dump(): String {
        val dumpedEntries = valueTypeByKey.map { (name, valueType) ->
            "(${name.dump()}): ${valueType.dump()}"
        }

        return "{${dumpedEntries.joinToString()}}"
    }

    override val keyType: PrimitiveType
        get() = TODO("key1 | key2 | ...")

    override val valueType: Type
        get() = TODO("value1 | value2 | ...")

    override fun isDefinitelyEmpty(): Boolean = valueTypeByKey.isEmpty()

    override val valueTypeByLabel: Map<Symbol, Type> = valueTypeByKey.mapNotNull { (key, value) ->
        (key as? Symbol)?.let { it to value }
    }.toMap()
}

// Type of tables with keys of a specific primitive type and values of a
// specific type, without more assumptions about them
// is never a struct
// is never an array
// (tangible keys!)
data class DictType(
    override val keyType: PrimitiveType,
    override val valueType: Type,
) : TableType() {
    override fun isAssignableTo(otherType: Type): Boolean {
        TODO("Not yet implemented")
    }

    override fun dump(): String = "{${keyType.dump()} ~> ${valueType.dump()}}"

    override fun isDefinitelyEmpty(): Boolean = false
}

//// Type of tables with keys { 0, 1, 2, ..., n } of type Int and values of a
//// specific type
//// is always struct
//// is always dict
//data class ArrayType(
//    override val valueType: Type,
//) : DictType() {
//    override val keyType: PrimitiveType = IntCollectiveType
//}

@Suppress("FunctionName")
fun ArrayType(
    elementType: Type,
) = DictType(
    keyType = IntCollectiveType,
    valueType = elementType,
)
