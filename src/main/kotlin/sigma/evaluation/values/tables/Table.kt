package sigma.evaluation.values.tables

import sigma.evaluation.Thunk
import sigma.evaluation.values.FunctionValue
import sigma.evaluation.values.UndefinedValue
import sigma.evaluation.values.Value

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
