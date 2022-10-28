package sigma

// Idea: Hierarchy like Expression -> Thunk -> Value?
sealed class Value : Thunk {
    final override fun obtain(): Value = this

    abstract fun dump(): String
}

sealed class PrimitiveValue : Value() {
    final override fun equals(other: Any?): Boolean {
        if (other !is Value) return false
        return isSame(other)
    }

    final override fun hashCode(): Int = 0

    abstract fun isSame(other: Value): Boolean
}
