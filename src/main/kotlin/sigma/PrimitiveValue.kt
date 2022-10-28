package sigma

sealed class PrimitiveValue : Value() {
    final override fun equals(other: Any?): Boolean {
        if (other !is Value) return false
        return isSame(other)
    }

    final override fun hashCode(): Int = 0

    abstract fun isSame(other: Value): Boolean
}
