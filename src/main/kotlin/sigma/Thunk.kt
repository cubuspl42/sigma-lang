package sigma

import sigma.values.Value

abstract class Thunk {
    override fun equals(other: Any?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun hashCode(): Int {
        throw UnsupportedOperationException()
    }

    override fun toString(): String = "Thunk"

    abstract fun obtain(): Value
}
