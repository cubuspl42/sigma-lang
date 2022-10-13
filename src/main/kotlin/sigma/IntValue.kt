package sigma

data class IntValue(
    val value: Int,
) : Value() {
    object Plus : FunctionValue() {
        override fun apply(argument: Value): Value {
            argument as FunctionValue

            val left = argument.apply(Symbol.of("left")) as IntValue
            val right = argument.apply(Symbol.of("right")) as IntValue

            return IntValue(left.value + right.value)
        }

        override fun dump(): String = "(+)"
    }

    object Minus : FunctionValue() {
        override fun apply(argument: Value): Value {
            argument as FunctionValue

            val left = argument.apply(Symbol.of("left")) as IntValue
            val right = argument.apply(Symbol.of("right")) as IntValue

            return IntValue(left.value - right.value)
        }

        override fun dump(): String = "(+)"
    }

    object Sq : FunctionValue() {
        override fun apply(argument: Value): Value {
            argument as IntValue

            return IntValue(argument.value * argument.value)
        }

        override fun dump(): String = "(+)"
    }

    object Eq : FunctionValue() {
        override fun apply(argument: Value): Value {
            argument as FunctionValue

            val left = argument.apply(Symbol.of("left")) as IntValue
            val right = argument.apply(Symbol.of("right")) as IntValue

            return BoolValue(left.value == right.value)
        }

        override fun dump(): String = "(+)"
    }

    override fun dump(): String = value.toString()
}
