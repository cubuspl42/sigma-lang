package sigma

// Idea: Hierarchy like Expression -> Thunk -> Value?
sealed class Value : Expression, Thunk {
    final override fun evaluate(context: Table): Value = this

    final override fun obtain(): Value = this
}

sealed class PrimitiveValue : Value() {
    final override fun equals(other: Any?): Boolean {
        if (other !is Value) return false
        return isSame(other)
    }

    final override fun hashCode(): Int = 0

    abstract fun isSame(other: Value): Boolean
}
