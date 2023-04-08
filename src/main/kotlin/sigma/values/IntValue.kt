package sigma.values

import sigma.BinaryOperationPrototype
import sigma.values.tables.Table

data class IntValue(
    val value: Long,
) : PrimitiveValue() {
    companion object {
        val Zero = IntValue(0)
    }

    abstract class BinaryIntFunction : ComputableFunctionValue() {
        override fun apply(argument: Value): Value {
            val argumentTuple = argument as Table

            val left = argumentTuple.read(Symbol.of(prototype.leftArgumentName))!!.toEvaluatedValue
            val right = argumentTuple.read(Symbol.of(prototype.rightArgumentName))!!.toEvaluatedValue

            left as IntValue
            right as IntValue

            return calculate(
                left = left.value,
                right = right.value,
            )
        }

        override fun dump(): String = "(${prototype.functionName})"

        abstract val prototype: BinaryOperationPrototype

        abstract fun calculate(left: Long, right: Long): Value
    }

    object Mul : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = BinaryOperationPrototype.multiplication

        override fun calculate(
            left: Long, right: Long,
        ): Value = IntValue(left * right)
    }

    object Div : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = BinaryOperationPrototype.division

        override fun calculate(
            left: Long, right: Long,
        ): Value = IntValue(left / right)
    }

    object Add : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = BinaryOperationPrototype.addition

        override fun calculate(
            left: Long, right: Long,
        ): Value = IntValue(left + right)
    }

    object Sub : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = BinaryOperationPrototype.subtraction

        override fun calculate(
            left: Long, right: Long,
        ): Value = IntValue(left - right)
    }

    object Sq : ComputableFunctionValue() {
        override fun apply(argument: Value): Value {
            val arg = (argument as Table).read(IntValue.Zero)!!.toEvaluatedValue as IntValue

            return IntValue(arg.value * arg.value)
        }

        override fun dump(): String = "(sq)"
    }

    object Eq : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = BinaryOperationPrototype.equals

        override fun calculate(
            left: Long,
            right: Long,
        ): Value = BoolValue(left == right)
    }

    object Lt : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = BinaryOperationPrototype.lessThan

        override fun calculate(
            left: Long,
            right: Long,
        ): Value = BoolValue(left < right)
    }

    object Lte : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = BinaryOperationPrototype.lessThanOrEqual

        override fun calculate(
            left: Long,
            right: Long,
        ): Value = BoolValue(left < right)
    }

    object Gt : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = BinaryOperationPrototype.greaterThan

        override fun calculate(
            left: Long,
            right: Long,
        ): Value = BoolValue(left > right)
    }

    object Gte : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = BinaryOperationPrototype.greaterThanOrEqual

        override fun calculate(
            left: Long,
            right: Long,
        ): Value = BoolValue(left >= right)
    }

    override fun dump(): String = value.toString()
}
