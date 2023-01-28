package sigma.types

sealed class FunctionType : Type() {
    abstract val metaArgumentType: TableType
    abstract val argumentType: Type
    abstract val imageType: Type
}

data class AbstractionType(
    override val metaArgumentType: TableType = TableType.Empty,
    override val argumentType: TupleType,
    override val imageType: Type,
) : FunctionType() {

    override fun dump(): String {
        val metaArgument = if (!metaArgumentType.isDefinitelyEmpty()) "!${metaArgumentType.dump()}" else null

        return listOfNotNull(
            metaArgument,
            "${argumentType.dump()} => ${imageType.dump()}",
        ).joinToString(
            separator = " ",
        )
    }
}
