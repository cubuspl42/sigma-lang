package sigma.values.tables

import sigma.Thunk
import sigma.values.Symbol

class ChainedScope(
    private val context: Scope,
    private val table: Table,
) : Scope() {
    override fun dumpContent(): String {
        val tableContent = table.dumpContent()
        val contextContent = context.dumpContent()

        return listOfNotNull(tableContent, contextContent).joinToString(separator = ", ")
    }

    override fun get(
        name: Symbol,
    ): Thunk? = table.read(
        argument = name,
    ) ?: context.get(
        name = name,
    )
}
