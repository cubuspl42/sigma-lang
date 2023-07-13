package sigma.evaluation.values

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

    // Idea: Rename to `key`?
    abstract fun read(
        argument: Value,
    ): Value?

    abstract fun dumpContent(): String?
}
