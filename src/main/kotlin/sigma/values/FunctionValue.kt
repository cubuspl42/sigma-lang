package sigma.values

import sigma.values.tables.ChainedTable
import sigma.values.tables.Table

abstract class FunctionValue : Value() {
    object Link : ComputableFunctionValue() {
        override fun apply(
            argument: Value,
        ): Value {
            argument as FunctionValue

            val primary = argument.apply(Symbol.of("primary")) as Table
            val secondary = argument.apply(Symbol.of("secondary")) as Table

            return ChainedTable(
                table = primary,
                context = secondary,
            )
        }

        override fun dump(): String = "(link function)"
    }

    abstract fun apply(
        argument: Value,
    ): Value
}
