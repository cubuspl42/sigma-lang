package sigma.expressions

import sigma.Thunk
import sigma.values.FunctionValue
import sigma.values.IntValue
import sigma.values.Symbol
import sigma.values.tables.Table
import sigma.values.Value
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser.BinaryOperationAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionDictAltContext
import kotlin.String

private var depth = 0

data class Application(
    val subject: Expression,
    val argument: Expression,
) : Expression {
    companion object {
        fun build(
            ctx: BinaryOperationAltContext,
        ): Application {
            val leftExpression = Expression.build(ctx.left)
            val rightExpression = Expression.build(ctx.right)

            val prototype = when (ctx.operator.type) {
                SigmaLexer.Asterisk -> IntValue.multiplication
                SigmaLexer.Plus -> IntValue.addition
                SigmaLexer.Minus -> IntValue.subtraction
                SigmaLexer.Slash -> IntValue.division
                SigmaLexer.Lt -> IntValue.lessThan
                SigmaLexer.Lte -> IntValue.lessThanOrEqual
                SigmaLexer.Gt -> IntValue.greaterThan
                SigmaLexer.Gte -> IntValue.greaterThanOrEqual
                SigmaLexer.Equals -> IntValue.equals
                SigmaLexer.Link -> IntValue.link
                else -> throw UnsupportedOperationException()
            }

            return Application(
                subject = Reference(
                    referee = Symbol.of(prototype.functionName),
                ),
                argument = DictConstructor.of(
                    mapOf(
                        SymbolLiteral(symbol = prototype.leftArgument) to leftExpression,
                        SymbolLiteral(symbol = prototype.rightArgument) to rightExpression,
                    ),
                ),
            )
        }

        fun build(
            ctx: CallExpressionAltContext,
        ): Application = Application(
            subject = Expression.build(ctx.callee),
            argument = Expression.build(ctx.argument),
        )

        fun build(
            ctx: CallExpressionDictAltContext,
        ): Application = Application(
            subject = Expression.build(ctx.callee),
            argument = DictConstructor.build(ctx.argument),
        )
    }

    override fun evaluate(
        context: Table,
    ): Thunk {
        val subjectValue = subject.evaluate(context = context)

        if (subjectValue !is FunctionValue) throw IllegalStateException("Subject $subjectValue is not a function")

        val argumentValue = argument.evaluate(context = context)

        val image = subjectValue.apply(
            argument = argumentValue.obtain(),
        )

        return image
    }

    override fun dump(): String = "${subject.dump()}[${argument.dump()}]"
}
