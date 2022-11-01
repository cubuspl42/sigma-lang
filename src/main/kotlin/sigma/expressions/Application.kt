package sigma.expressions

import sigma.BinaryOperationPrototype
import sigma.StaticScope
import sigma.Thunk
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser.BinaryOperationAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionDictAltContext
import sigma.types.AbstractionType
import sigma.types.Type
import sigma.values.FunctionValue
import sigma.values.Symbol
import sigma.values.TypeError
import sigma.values.tables.Scope

private var depth = 0

data class Application(
    val subject: Expression,
    val argument: Expression,
) : Expression() {
    companion object {
        fun build(
            ctx: BinaryOperationAltContext,
        ): Application {
            val leftExpression = Expression.build(ctx.left)
            val rightExpression = Expression.build(ctx.right)

            val prototype = when (ctx.operator.type) {
                SigmaLexer.Asterisk -> BinaryOperationPrototype.multiplication
                SigmaLexer.Plus -> BinaryOperationPrototype.addition
                SigmaLexer.Minus -> BinaryOperationPrototype.subtraction
                SigmaLexer.Slash -> BinaryOperationPrototype.division
                SigmaLexer.Lt -> BinaryOperationPrototype.lessThan
                SigmaLexer.Lte -> BinaryOperationPrototype.lessThanOrEqual
                SigmaLexer.Gt -> BinaryOperationPrototype.greaterThan
                SigmaLexer.Gte -> BinaryOperationPrototype.greaterThanOrEqual
                SigmaLexer.Equals -> BinaryOperationPrototype.equals
                SigmaLexer.Link -> BinaryOperationPrototype.link
                else -> throw UnsupportedOperationException()
            }

            return Application(
                subject = Reference(
                    referee = Symbol.of(prototype.functionName),
                ),
                argument = TableConstructor(
                    entries = listOf(
                        TableConstructor.SymbolEntryExpression(
                            name = prototype.leftArgument,
                            value = leftExpression,
                        ),
                        TableConstructor.SymbolEntryExpression(
                            name = prototype.rightArgument,
                            value = rightExpression,
                        ),
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
            argument = TableConstructor.build(ctx.argument),
        )
    }

    override fun inferType(scope: StaticScope): Type {
        val subjectType = subject.inferType(
            scope = scope,
        ) as? AbstractionType ?: throw TypeError(
            message = "Only functions can be called",
        )

        return subjectType.imageType
    }

    override fun evaluate(
        scope: Scope,
    ): Thunk {
        val subjectValue = subject.evaluate(scope = scope).obtain()

        if (subjectValue !is FunctionValue) throw IllegalStateException("Subject $subjectValue is not a function")

        val argumentValue = argument.evaluate(scope = scope)

        // Thought: Obtaining argument here might not be lazy enough
        val image = subjectValue.apply(
            argument = argumentValue.obtain(),
        )

        return image
    }

    override fun dump(): String = "${subject.dump()}[${argument.dump()}]"
}
