package sigma.evaluation

import sigma.evaluation.values.Value

abstract class Thunk {
    override fun equals(other: Any?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun hashCode(): Int {
        throw UnsupportedOperationException()
    }

    override fun toString(): String = dump()

    abstract val toEvaluatedValue: Value

    abstract fun dump(): String
}
