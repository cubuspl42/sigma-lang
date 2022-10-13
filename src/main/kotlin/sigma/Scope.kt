package sigma

abstract class Scope : FunctionValue() {
    override fun apply(
        argument: Value,
    ): Value = get(
        name = argument as Symbol,
    )

    abstract fun get(name: Symbol): Value
}
