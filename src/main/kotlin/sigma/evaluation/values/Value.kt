package sigma.evaluation.values

import sigma.Thunk

// Thought: Rename to `EvaluatedValue`?
sealed class Value : Thunk() {
    final override val toEvaluatedValue: Value
        get() = this
}
