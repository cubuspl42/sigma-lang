package sigma.types

// Type of tables with keys of a single primitive type and values of a single
// specific type
data class DictType(
    override val keyType: PrimitiveType,
    override val valueType: Type,
) : TableType() {

    override fun dump(): String = "{${keyType.dump()} ~> ${valueType.dump()}}"

    override fun isDefinitelyEmpty(): Boolean = false
}
