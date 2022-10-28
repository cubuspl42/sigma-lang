package sigma.values

import sigma.Thunk

sealed class Value : Thunk() {
    final override fun obtain(): Value = this

    abstract fun dump(): String
}
