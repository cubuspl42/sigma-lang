package sigma.values

import sigma.Thunk

abstract class FunctionValue : Value() {
    object Link : ComputableFunctionValue() {
        override fun apply(
            argument: Value,
        ): Value {
            argument as FunctionValue

            val primary = argument.apply(
                argument = Symbol.of("primary"),
            ).toEvaluatedValue as FunctionValue

            val secondary = argument.apply(
                argument = Symbol.of("secondary"),
            ).toEvaluatedValue as FunctionValue

            return object : FunctionValue() {
                override fun apply(argument: Value): Thunk {
                    val value = primary.apply(argument = argument)

                    return when (value) {
                        is UndefinedValue -> secondary.apply(argument = argument)
                        else -> value
                    }
                }

                override fun dump(): String = "${primary.dump()} .. ${secondary.dump()}"
            }
        }

        override fun dump(): String = "(link function)"
    }

    abstract fun apply(
        argument: Value,
    ): Thunk
}
