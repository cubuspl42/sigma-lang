package sigma.types

data class ArrayType(
   val elementType: Type,
) : TableType() {
    override val keyType = IntCollectiveType

    override val valueType: Type = elementType

    override fun isDefinitelyEmpty(): Boolean = false

    override fun isAssignableTo(otherType: Type): Boolean {
        TODO("Not yet implemented")
    }

    override fun dump(): String = "[${elementType.dump()}*]"
}
