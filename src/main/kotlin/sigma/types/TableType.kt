package sigma.types

sealed class TableType : FunctionType() {
    object Empty : TableType() {
        override val keyType: PrimitiveType = NeverType

        override val valueType: Type = NeverType

        override fun isDefinitevlyEmpty(): Boolean = true

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

    abstract fun isDefinitevlyEmpty(): Boolean
}

// TODO: Simplify this, at least for now. Add union types and iterate
// Struct and dict types should be enough

// Type of tables with fixed number of entries, with keys of value types, and
// values of respective types
// is sometimes dict (if values are consistent)
// is sometimes array (if keys are consecutive)
data class StructType(
    val entries: Map<LiteralType, Type>,
) : TableType() {
    override fun isAssignableTo(otherType: Type): Boolean {
        TODO("Not yet implemented")
    }

    override fun dump(): String {
        val dumpedEntries = entries.map { (entryKeyLiteralType, valueType) ->
            val entryKeyType = entryKeyLiteralType.asType

            "(${entryKeyType.dump()}): ${valueType.dump()}"
        }

        return "{${dumpedEntries.joinToString()}}"
    }

    override val keyType: PrimitiveType
        get() = TODO("key1 | key2 | ...")

    override val valueType: Type
        get() = TODO("value1 | value2 | ...")

    override fun isDefinitevlyEmpty(): Boolean = entries.isEmpty()
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

    override fun isDefinitevlyEmpty(): Boolean = false
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
