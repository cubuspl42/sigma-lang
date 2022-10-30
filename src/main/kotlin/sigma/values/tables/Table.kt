package sigma.values.tables

import sigma.Thunk
import sigma.values.FunctionValue
import sigma.values.UndefinedValue
import sigma.values.Value

// Thought: Would PartialFunction be a better name?
abstract class Table : FunctionValue() {
    final override fun apply(
        argument: Value,
    ): Thunk = read(
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

    // Idea: Rename to `key`?
    abstract fun read(
        argument: Value,
    ): Thunk?

    abstract fun dumpContent(): String?
}
