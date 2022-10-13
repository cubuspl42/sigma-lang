package sigma

// Thought: Would PartialFunction be a better name?
abstract class Table : FunctionValue() {
    final override fun apply(
        argument: Value,
    ): Value = read(
        argument = argument,
    ) ?: UndefinedValue(argument)

    abstract fun read(
        argument: Value,
    ): Value?

    final override fun dump(): String {
        val content = dumpContent()

        return when {
            content != null -> "{ $content }"
            else -> "∅"
        }
    }

    final override fun isSame(other: Value): Boolean {
        if (other !is Table) return false

        return this.isSubsetOf(other) && other.isSubsetOf(this)
    }

    abstract fun isSubsetOf(other: FunctionValue): Boolean

    abstract fun dumpContent(): String?
}
