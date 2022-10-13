package sigma

import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser.BinaryOperationAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionAltContext
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
                        Symbol.of(prototype.leftArgumentName) to leftExpression,
                        Symbol.of(prototype.rightArgumentName) to rightExpression,
                    ),
                ),
            )
        }

        fun build(
            read: CallExpressionAltContext,
        ): Application = Application(
            subject = Expression.build(read.callee),
            argument = Expression.build(read.argument),
        )
    }

    override fun evaluate(
        context: Table,
    ): Value {
        ++depth

//        if (depth > 1000) {
//            println("Going deep...")
//        }

        val subjectValue = subject.evaluate(context = context)

        if (subjectValue !is FunctionValue) throw IllegalStateException("Subject $subjectValue is not a function")

        val argumentValue = argument.evaluate(context = context)

//        println("Calling ${subject.dump()} with argument ${argumentValue.dump()}")

        val image = subjectValue.apply(
            argument = argumentValue,
        ) ?: throw IllegalStateException("Subject is not defined over ${argumentValue.dump()}")

        --depth

        return image
    }

    override fun dump(): String = "${subject.dump()}[${argument.dump()}]"
}
