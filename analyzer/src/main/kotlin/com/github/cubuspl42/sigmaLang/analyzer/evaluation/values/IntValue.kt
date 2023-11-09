package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.semantics.BinaryOperator

data class IntValue(
    val value: Long,
) : PrimitiveValue() {
    companion object {
        val Zero = IntValue(0)
    }

    abstract class BinaryIntFunction : ComputableFunctionValue() {
        override fun apply(argument: Value): Thunk<Value> {
            val argumentTuple = argument as DictValue

            val left = argumentTuple.read(Identifier.of(prototype.leftArgumentName))!!
            val right = argumentTuple.read(Identifier.of(prototype.rightArgumentName))!!

            val leftValue = left.value as IntValue
            val rightValue = right.value as IntValue

            return calculate(
                left = leftValue.value,
                right = rightValue.value,
            ).toThunk()
        }

        override fun dump(): String = "(${prototype.functionName})"

        abstract val prototype: BinaryOperator

        abstract fun calculate(left: Long, right: Long): Value
    }

    object Mul : BinaryIntFunction() {
        override val prototype: BinaryOperator = BinaryOperator.multiplication

        override fun calculate(
            left: Long, right: Long,
        ): Value = IntValue(left * right)
    }

    object Div : BinaryIntFunction() {
        override val prototype: BinaryOperator = BinaryOperator.division

        override fun calculate(
            left: Long, right: Long,
        ): Value = IntValue(left / right)
    }

    object Add : BinaryIntFunction() {
        override val prototype: BinaryOperator = BinaryOperator.addition

        override fun calculate(
            left: Long, right: Long,
        ): Value = IntValue(left + right)
    }

    object Sub : BinaryIntFunction() {
        override val prototype: BinaryOperator = BinaryOperator.subtraction

        override fun calculate(
            left: Long, right: Long,
        ): Value = IntValue(left - right)
    }

    object Sq : ComputableFunctionValue() {
        override fun apply(argument: Value): Thunk<Value> {
            val arg = (argument as DictValue).readValue(Zero)!! as IntValue

            return IntValue(arg.value * arg.value).toThunk()
        }

        override fun dump(): String = "(sq)"
    }

    object Eq : BinaryIntFunction() {
        override val prototype: BinaryOperator = BinaryOperator.equals

        override fun calculate(
            left: Long,
            right: Long,
        ): Value = BoolValue(left == right)
    }

    object Lt : BinaryIntFunction() {
        override val prototype: BinaryOperator = BinaryOperator.lessThan

        override fun calculate(
            left: Long,
            right: Long,
        ): Value = BoolValue(left < right)
    }

    object Lte : BinaryIntFunction() {
        override val prototype: BinaryOperator = BinaryOperator.lessThanOrEqual

        override fun calculate(
            left: Long,
            right: Long,
        ): Value = BoolValue(left < right)
    }

    object Gt : BinaryIntFunction() {
        override val prototype: BinaryOperator = BinaryOperator.greaterThan

        override fun calculate(
            left: Long,
            right: Long,
        ): Value = BoolValue(left > right)
    }

    object Gte : BinaryIntFunction() {
        override val prototype: BinaryOperator = BinaryOperator.greaterThanOrEqual

        override fun calculate(
            left: Long,
            right: Long,
        ): Value = BoolValue(left >= right)
    }

    override fun dump(): String = value.toString()
}
