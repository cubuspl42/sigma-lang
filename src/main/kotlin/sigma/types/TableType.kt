package sigma.types

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
