package sigma

import sigma.expressions.Expression

// Idea: Hierarchy like Expression -> Thunk -> Value?
sealed class Value : Thunk {
    final override fun obtain(): Value = this

    abstract fun dump(): String
}
