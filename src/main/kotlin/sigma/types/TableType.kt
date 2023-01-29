package sigma.types

sealed class TableType : FunctionType() {
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
