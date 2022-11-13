package sigma.expressions

import org.antlr.v4.runtime.ParserRuleContext
import sigma.BinaryOperationPrototype
import sigma.StaticTypeScope
import sigma.StaticValueScope

import sigma.Thunk
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser.BinaryOperationAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionTupleLiteralAltContext
import sigma.types.AbstractionType
import sigma.types.Type
import sigma.values.FunctionValue
import sigma.values.Symbol
import sigma.values.TypeError
import sigma.values.tables.Scope

private var depth = 0

data class SourceLocation(
    val lineIndex: Int,
    val columnIndex: Int,
) {
    companion object {
        // TODO: It's a hack, figure this out
        val Invalid = SourceLocation(
            lineIndex = -1,
            columnIndex = -1,
        )

        fun build(
            ctx: ParserRuleContext,
        ): SourceLocation = SourceLocation(
            lineIndex = ctx.start.line,
            columnIndex = ctx.start.charPositionInLine,
        )
    }

    override fun toString(): String = "[Ln ${lineIndex}, Col ${columnIndex}]"
}

data class Call(
    override val location: SourceLocation,
    val subject: Expression,
    val argument: Expression,
) : Expression() {
    companion object {
        fun build(
            ctx: BinaryOperationAltContext,
        ): Call {
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

            return Call(
                location = SourceLocation.build(ctx),
                subject = Reference(
                    location = SourceLocation.build(ctx),
                    referee = Symbol.of(prototype.functionName),
                ),
                argument = UnorderedTupleLiteral(
                    location = SourceLocation.build(ctx),
                    entries = listOf(
                        UnorderedTupleLiteral.NamedEntryExpression(
                            name = prototype.leftArgument,
                            value = leftExpression,
                        ),
                        UnorderedTupleLiteral.NamedEntryExpression(
                            name = prototype.rightArgument,
                            value = rightExpression,
                        ),
                    ),
                ),
            )
        }

        fun build(
            ctx: CallExpressionAltContext,
        ): Call = Call(
            location = SourceLocation.build(ctx),
            subject = Expression.build(ctx.callee),
            argument = Expression.build(ctx.argument),
        )

        fun build(
            ctx: CallExpressionTupleLiteralAltContext,
        ): Call = Call(
            location = SourceLocation.build(ctx),
            subject = Expression.build(ctx.callee),
            argument = TupleLiteral.build(ctx.argument),
        )
    }

    override fun inferType(typeScope: StaticTypeScope, valueScope: StaticValueScope): Type {
        val subjectType = subject.inferType(
            typeScope = typeScope,
            valueScope = valueScope,
        ) as? AbstractionType ?: throw TypeError(
            location = location,
            message = "Only functions can be called",
        )

        return subjectType.imageType
    }

    override fun evaluate(
        scope: Scope,
    ): Thunk {
        val subjectValue = subject.evaluate(scope = scope).toEvaluatedValue

        if (subjectValue !is FunctionValue) throw IllegalStateException("Subject $subjectValue is not a function")

        val argumentValue = argument.evaluate(scope = scope)

        // Thought: Obtaining argument here might not be lazy enough
        val image = subjectValue.apply(
            argument = argumentValue.toEvaluatedValue,
        )

        return image
    }

    override fun dump(): String = "${subject.dump()}[${argument.dump()}]"
}
