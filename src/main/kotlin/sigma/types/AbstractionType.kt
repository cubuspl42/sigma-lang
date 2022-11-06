package sigma.types

sealed class FunctionType : Type() {
    abstract val metaArgumentType: TableType
    abstract val argumentType: Type
    abstract val imageType: Type
}

data class AbstractionType(
    override val metaArgumentType: TableType = TableType.Empty,
    override val argumentType: Type,
    override val imageType: Type,
) : FunctionType() {
    override fun isAssignableTo(otherType: Type): Boolean {
        TODO("Not yet implemented")
    }

    override fun dump(): String {
        val metaArgument = if (!metaArgumentType.isDefinitevlyEmpty()) "!${metaArgumentType.dump()}" else null

        return listOfNotNull(
            metaArgument,
            "${argumentType.dump()} => ${imageType.dump()}",
        ).joinToString(
            separator = " ",
        )
    }
}
