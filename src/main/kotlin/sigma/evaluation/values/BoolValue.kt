package sigma.evaluation.values

data class BoolValue(
    val value: Boolean,
) : PrimitiveValue() {
    companion object {
        val False = BoolValue(false)

        val True = BoolValue(true)
    }

    object If : ComputableFunctionValue() {
        override fun apply(argument: Value): Value {
            val test = (argument as Table).read(IntValue.Zero)!! as BoolValue

            return object : ComputableFunctionValue() {
                override fun apply(argument: Value): Value {
                    val branches = argument as FunctionValue

                    return when {
                        test.value -> branches.apply(Symbol.of("then"))
                        else -> branches.apply(Symbol.of("else"))
                    }
                }

                override fun dump(): String = "(if')"
            }
        }

        override fun dump(): String = "(if)"
    }

    override fun dump(): String = value.toString()
}
