package sigma.evaluation.values

import sigma.BinaryOperationPrototype
import sigma.semantics.expressions.EvaluationContext

data class IntValue(
    val value: Long,
) : PrimitiveValue() {
    companion object {
        val Zero = IntValue(0)
    }

    abstract class BinaryIntFunction : ComputableFunctionValue() {
        override fun apply(context: EvaluationContext, argument: Value): EvaluationResult {
            val argumentTuple = argument as DictValue

            val left = argumentTuple.read(Symbol.of(prototype.leftArgumentName))!!
            val right = argumentTuple.read(Symbol.of(prototype.rightArgumentName))!!

            left as IntValue
            right as IntValue

            return calculate(
                left = left.value,
                right = right.value,
            ).asEvaluationResult
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
        override fun apply(context: EvaluationContext, argument: Value): EvaluationResult {
            val arg = (argument as DictValue).read(Zero)!! as IntValue

            return IntValue(arg.value * arg.value).asEvaluationResult
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
