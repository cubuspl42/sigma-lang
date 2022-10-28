package sigma

data class IntValue(
    val value: Int,
) : PrimitiveValue() {
    data class BinaryOperationPrototype(
        val functionName: String,
        val leftArgumentName: String,
        val rightArgumentName: String,
    ) {
        val leftArgument: Symbol
            get() = Symbol.of(leftArgumentName)

        val rightArgument: Symbol
            get() = Symbol.of(rightArgumentName)
    }

    companion object {
        val multiplication = BinaryOperationPrototype(
            functionName = "mul",
            leftArgumentName = "multiplier",
            rightArgumentName = "multiplicand"
        )

        val addition = BinaryOperationPrototype(
            functionName = "add",
            leftArgumentName = "augend",
            rightArgumentName = "addend"
        )

        val subtraction = BinaryOperationPrototype(
            functionName = "sub",
            leftArgumentName = "minuend",
            rightArgumentName = "subtrahend"
        )

        val division = BinaryOperationPrototype(
            functionName = "div",
            leftArgumentName = "dividend",
            rightArgumentName = "divisor"
        )

        val lessThan = BinaryOperationPrototype(
            functionName = "lt",
            leftArgumentName = "left",
            rightArgumentName = "right"
        )

        val lessThanOrEqual = BinaryOperationPrototype(
            functionName = "lt",
            leftArgumentName = "left",
            rightArgumentName = "right"
        )

        val greaterThan = BinaryOperationPrototype(
            functionName = "gt",
            leftArgumentName = "left",
            rightArgumentName = "right"
        )

        val greaterThanOrEqual = BinaryOperationPrototype(
            functionName = "gte",
            leftArgumentName = "left",
            rightArgumentName = "right"
        )

        val equals = BinaryOperationPrototype(
            functionName = "eq",
            leftArgumentName = "first",
            rightArgumentName = "second"
        )

        val link = BinaryOperationPrototype(
            functionName = "link",
            leftArgumentName = "primary",
            rightArgumentName = "secondary"
        )
    }

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
        override val prototype: BinaryOperationPrototype = multiplication

        override fun calculate(
            left: Int, right: Int,
        ): Value = IntValue(left * right)
    }

    object Div : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = division

        override fun calculate(
            left: Int, right: Int,
        ): Value = IntValue(left / right)
    }

    object Add : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = addition

        override fun calculate(
            left: Int, right: Int,
        ): Value = IntValue(left + right)
    }

    object Sub : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = subtraction

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
        override val prototype: BinaryOperationPrototype = equals

        override fun calculate(
            left: Int,
            right: Int,
        ): Value = BoolValue(left == right)
    }

    object Lt : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = lessThan

        override fun calculate(
            left: Int,
            right: Int,
        ): Value = BoolValue(left < right)
    }

    object Lte : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = lessThanOrEqual

        override fun calculate(
            left: Int,
            right: Int,
        ): Value = BoolValue(left < right)
    }

    object Gt : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = greaterThan

        override fun calculate(
            left: Int,
            right: Int,
        ): Value = BoolValue(left > right)
    }

    object Gte : BinaryIntFunction() {
        override val prototype: BinaryOperationPrototype = greaterThanOrEqual

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
