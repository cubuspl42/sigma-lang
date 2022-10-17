package sigma

// Idea: Hierarchy like Expression -> Thunk -> Value?
sealed class Value : Expression, Thunk {
    final override fun equals(other: Any?): Boolean {
        if (other !is Value) return false
        return isSame(other)
    }

    final override fun hashCode(): Int = 0

    final override fun toString(): String = dump()

    final override fun evaluate(context: Table): Value = this

    final override fun obtain(): Value = this

    abstract fun isSame(other: Value): Boolean
}
