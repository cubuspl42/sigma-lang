package sigma

data class BoolValue(
    val value: Boolean,
) : Value() {
    companion object {
        val False = BoolValue(false)

        val True = BoolValue(true)
    }

    object If : FunctionValue() {
        override fun apply(argument: Value): Value {
            argument as FunctionValue

            val test = argument.apply(Symbol.of("test")) as BoolValue

            return when {
                test.value -> argument.apply(Symbol.of("then"))
                else -> argument.apply(Symbol.of("else"))
            }
        }

        override fun dump(): String = "(+)"
    }

    override fun dump(): String = value.toString()
}
