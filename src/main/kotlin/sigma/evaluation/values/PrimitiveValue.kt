package sigma.evaluation.values

sealed class PrimitiveValue : Value() {
    override fun equalsTo(other: Value): Boolean = this == other
}