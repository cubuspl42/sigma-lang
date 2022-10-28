package sigma

import sigma.values.Symbol

data class BinaryOperationPrototype(
    val functionName: String,
    val leftArgumentName: String,
    val rightArgumentName: String,
) {
    companion object {
        val multiplication = BinaryOperationPrototype(
            functionName = "mul", leftArgumentName = "multiplier", rightArgumentName = "multiplicand"
        )

        val addition = BinaryOperationPrototype(
            functionName = "add", leftArgumentName = "augend", rightArgumentName = "addend"
        )

        val subtraction = BinaryOperationPrototype(
            functionName = "sub", leftArgumentName = "minuend", rightArgumentName = "subtrahend"
        )

        val division = BinaryOperationPrototype(
            functionName = "div", leftArgumentName = "dividend", rightArgumentName = "divisor"
        )

        val lessThan = BinaryOperationPrototype(
            functionName = "lt", leftArgumentName = "left", rightArgumentName = "right"
        )

        val lessThanOrEqual = BinaryOperationPrototype(
            functionName = "lt", leftArgumentName = "left", rightArgumentName = "right"
        )

        val greaterThan = BinaryOperationPrototype(
            functionName = "gt", leftArgumentName = "left", rightArgumentName = "right"
        )

        val greaterThanOrEqual = BinaryOperationPrototype(
            functionName = "gte", leftArgumentName = "left", rightArgumentName = "right"
        )

        val equals = BinaryOperationPrototype(
            functionName = "eq", leftArgumentName = "first", rightArgumentName = "second"
        )

        val link = BinaryOperationPrototype(
            functionName = "link", leftArgumentName = "primary", rightArgumentName = "secondary"
        )
    }

    val leftArgument: Symbol
        get() = Symbol.of(leftArgumentName)

    val rightArgument: Symbol
        get() = Symbol.of(rightArgumentName)
}
