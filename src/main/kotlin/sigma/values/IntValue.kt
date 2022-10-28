package sigma.values

import sigma.BinaryOperationPrototype

data class IntValue(
    val value: Int,
) : PrimitiveValue() {

    abstract class BinaryIntFunction : ComputableFunctionValue() {
        override fun apply(argument: Value): Value {
            argument as FunctionValue

            val left = argument.apply(Symbol.of(prototype.leftArgumentName))
            val right = argument.apply(Symbol.of(prototype.rightArgumentName))

            left as IntValue
            right as IntValue

            return calculate(
                left = left.value,
                right = right.value,
            )
        }

        override fun dump(): String = "(${prototype.functionName})"

        abstract val prototype: BinaryOperationPrototype

        abstract fun calculate(left: Int, right: Int): Value
    }

    object Mul : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = BinaryOperationPrototype.multiplication

        override fun calculate(
            left: Int, right: Int,
        ): Value = IntValue(left * right)
    }

    object Div : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = BinaryOperationPrototype.division

        override fun calculate(
            left: Int, right: Int,
        ): Value = IntValue(left / right)
    }

    object Add : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = BinaryOperationPrototype.addition

        override fun calculate(
            left: Int, right: Int,
        ): Value = IntValue(left + right)
    }

    object Sub : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = BinaryOperationPrototype.subtraction

        override fun calculate(
            left: Int, right: Int,
        ): Value = IntValue(left - right)
    }

    object Sq : ComputableFunctionValue() {
        override fun apply(argument: Value): Value {
            argument as IntValue

            return IntValue(argument.value * argument.value)
        }

        override fun dump(): String = "(sq)"
    }

    object Eq : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = BinaryOperationPrototype.equals

        override fun calculate(
            left: Int,
            right: Int,
        ): Value = BoolValue(left == right)
    }

    object Lt : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = BinaryOperationPrototype.lessThan

        override fun calculate(
            left: Int,
            right: Int,
        ): Value = BoolValue(left < right)
    }

    object Lte : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = BinaryOperationPrototype.lessThanOrEqual

        override fun calculate(
            left: Int,
            right: Int,
        ): Value = BoolValue(left < right)
    }

    object Gt : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = BinaryOperationPrototype.greaterThan

        override fun calculate(
            left: Int,
            right: Int,
        ): Value = BoolValue(left > right)
    }

    object Gte : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = BinaryOperationPrototype.greaterThanOrEqual

        override fun calculate(
            left: Int,
            right: Int,
        ): Value = BoolValue(left >= right)
    }

    override fun isSame(other: Value): Boolean {
        if (other !is IntValue) return false
        return value == other.value
    }

    override fun dump(): String = value.toString()
}
