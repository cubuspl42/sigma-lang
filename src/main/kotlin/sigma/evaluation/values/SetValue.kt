package sigma.evaluation.values

data class SetValue(
    val elements: Set<Value>,
) : Value() {
    override fun dump(): String {
        TODO("Not yet implemented")
    }
}
