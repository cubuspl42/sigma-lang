package sigma.values.tables

import sigma.values.FunctionValue
import sigma.values.UndefinedValue
import sigma.values.Value

// Thought: Would PartialFunction be a better name?
abstract class Table : FunctionValue() {
    final override fun apply(
        argument: Value,
    ): Value = read(
        argument = argument,
    ) ?: UndefinedValue.withName(
        name = argument,
    )

    final override fun dump(): String {
        val content = dumpContent()

        return when {
            content != null -> "{ $content }"
            else -> "∅"
        }
    }

    fun chainWith(
        context: Table,
    ): Table = ChainedTable(
        context = context,
        table = this,
    )

    // Idea: Rename to `key`?
    abstract fun read(
        argument: Value,
    ): Value?

    abstract fun dumpContent(): String?
}
