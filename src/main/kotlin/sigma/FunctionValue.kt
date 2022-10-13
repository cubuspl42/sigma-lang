package sigma

sealed class FunctionValue: Value() {
    abstract fun apply(
        argument: Value,
    ): Value
}
