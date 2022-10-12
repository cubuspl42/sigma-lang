package sigma

sealed class Value : Expression {
    override fun evaluate(scope: Scope): Value = this
}

sealed class FunctionValue : Value() {
    abstract fun apply(
        argument: Value,
    ): Value
}
