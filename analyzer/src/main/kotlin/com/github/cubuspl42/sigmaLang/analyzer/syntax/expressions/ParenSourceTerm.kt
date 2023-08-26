package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.ParenExpressionContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class ParenSourceTerm(
    override val location: SourceLocation,
    override val wrappedTerm: ExpressionSourceTerm,
) : ExpressionSourceTerm(), ParenTerm {
    companion object {
        fun build(
            ctx: ParenExpressionContext,
        ): ParenSourceTerm = ParenSourceTerm(
            location = SourceLocation.build(ctx),
            wrappedTerm = ExpressionSourceTerm.build(ctx.expression()),
        )
    }

    override fun dump(): String = "(${wrappedTerm.dump()})"
}
