package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.IfExpressionContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class IfExpressionSourceTerm(
    override val location: SourceLocation,
    override val guard: ExpressionTerm,
    override val trueBranch: ExpressionTerm,
    override val falseBranch: ExpressionTerm,
) : ExpressionSourceTerm(), IfExpressionTerm {
    companion object {
        fun build(
            ctx: IfExpressionContext,
        ): IfExpressionSourceTerm = IfExpressionSourceTerm(
            location = SourceLocation.build(ctx),
            guard = ExpressionSourceTerm.build(ctx.guard),
            trueBranch = ExpressionSourceTerm.build(ctx.trueBranch),
            falseBranch = ExpressionSourceTerm.build(ctx.falseBranch),
        )
    }

    override fun dump(): String = """
        if (${guard.dump()}) (
            %then ${trueBranch.dump()},
            %else ${falseBranch.dump()},
        )
    """.trimIndent()
}
