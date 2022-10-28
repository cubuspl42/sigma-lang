package sigma

// Idea: Hierarchy like Expression -> Thunk -> Value?
sealed class Value : Thunk {
    final override fun obtain(): Value = this

    abstract fun dump(): String
}
