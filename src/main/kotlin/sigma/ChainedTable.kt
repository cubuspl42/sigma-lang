package sigma

class ChainedTable(
    private val context: Table,
    private val table: Table,
) : Table() {
    override fun dumpContent(): String {
        val tableContent = table.dumpContent()
        val contextContent = context.dumpContent()

        return listOfNotNull(tableContent, contextContent).joinToString(separator = ", ")
    }

    override fun read(
        argument: Value,
    ): Value? = table.read(argument = argument) ?: context.read(argument = argument)
}
