package sigma.values

import sigma.Thunk

sealed class Value : Thunk() {
    final override val toEvaluatedValue: Value
        get() = this
}
