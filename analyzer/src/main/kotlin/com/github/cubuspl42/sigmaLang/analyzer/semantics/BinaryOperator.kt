package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Call
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.CallTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.InfixCallTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.InfixOperator
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorTerm

data class BinaryOperator(
    val functionName: String,
    val leftArgumentName: String,
    val rightArgumentName: String,
) {
    companion object {
        val multiplication = BinaryOperator(
            functionName = "mul", leftArgumentName = "multiplier", rightArgumentName = "multiplicand"
        )

        val addition = BinaryOperator(
            functionName = "add", leftArgumentName = "augend", rightArgumentName = "addend"
        )

        val subtraction = BinaryOperator(
            functionName = "sub", leftArgumentName = "minuend", rightArgumentName = "subtrahend"
        )

        val division = BinaryOperator(
            functionName = "div", leftArgumentName = "dividend", rightArgumentName = "divisor"
        )

        val lessThan = BinaryOperator(
            functionName = "lt", leftArgumentName = "left", rightArgumentName = "right"
        )

        val lessThanOrEqual = BinaryOperator(
            functionName = "lt", leftArgumentName = "left", rightArgumentName = "right"
        )

        val greaterThan = BinaryOperator(
            functionName = "gt", leftArgumentName = "left", rightArgumentName = "right"
        )

        val greaterThanOrEqual = BinaryOperator(
            functionName = "gte", leftArgumentName = "left", rightArgumentName = "right"
        )

        val equals = BinaryOperator(
            functionName = "eq", leftArgumentName = "first", rightArgumentName = "second"
        )

        val link = BinaryOperator(
            functionName = "link", leftArgumentName = "primary", rightArgumentName = "secondary"
        )

        fun build(
            operator: InfixOperator,
        ): BinaryOperator = when (operator) {
            InfixOperator.Multiply -> multiplication
            InfixOperator.Add -> addition
            InfixOperator.Subtract -> subtraction
            InfixOperator.Divide -> division
            InfixOperator.LessThan -> lessThan
            InfixOperator.LessThanEqual -> lessThanOrEqual
            InfixOperator.GreaterThan -> greaterThan
            InfixOperator.GreaterThanEqual -> greaterThanOrEqual
            InfixOperator.Equals -> equals
            InfixOperator.Link -> link
        }
    }

    val functionNameIdentifier: Identifier
        get() = Identifier.of(functionName)

    val leftArgument: Identifier
        get() = Identifier.of(leftArgumentName)

    val rightArgument: Identifier
        get() = Identifier.of(rightArgumentName)

    fun buildCall(
        context: Expression.BuildContext,
        term: InfixCallTerm,
        leftArgument: Expression,
        rightArgument: Expression,
    ): Lazy<Call> = lazy {
        val buildOutput = ReferenceTerm.build(
            context,
            term = term,
            referredName = Symbol.of(functionName),
        )

        object : Call() {
            override val outerScope: StaticScope = context.outerScope

            override val term: CallTerm = term

            override val subject: Expression by buildOutput.expressionLazy

            override val argument: Expression by lazy {
                object : UnorderedTupleConstructor() {
                    override val outerScope: StaticScope = context.outerScope

                    override val term: UnorderedTupleConstructorTerm? = null

                    override val entries: Set<Entry> = setOf(
                        object : Entry() {
                            override val name: Symbol = this@BinaryOperator.leftArgument

                            override val value: Expression = leftArgument
                        },
                        object : Entry() {
                            override val name: Symbol = this@BinaryOperator.rightArgument

                            override val value: Expression = rightArgument
                        },
                    )
                }
            }
        }
    }
}
