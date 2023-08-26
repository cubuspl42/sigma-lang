package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.BinaryOperationAltContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class InfixCallSourceTerm(
    override val location: SourceLocation,
    override val operator: InfixOperator,
    override val leftArgument: ExpressionSourceTerm,
    override val rightArgument: ExpressionSourceTerm,
) : CallSourceTerm(), InfixCallTerm {
    companion object {
        fun build(
            ctx: BinaryOperationAltContext,
        ): InfixCallSourceTerm {
            val operator = InfixOperator.build(ctx.operator)
            val leftArgument = ExpressionSourceTerm.build(ctx.left)
            val rightArgument = ExpressionSourceTerm.build(ctx.right)

            return InfixCallSourceTerm(
                location = SourceLocation.build(ctx),
                operator = operator,
                leftArgument = leftArgument,
                rightArgument = rightArgument,
            )
        }
    }

    override fun dump(): String = "(${leftArgument.dump()} ${operator.symbol} ${rightArgument.dump()})"
}
