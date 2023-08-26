package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.IsUndefinedCheckContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class IsUndefinedCheckSourceTerm(
    override val location: SourceLocation,
    override val argument: ExpressionTerm,
) : ExpressionSourceTerm(), IsUndefinedCheckTerm {
    companion object {
        fun build(
            ctx: IsUndefinedCheckContext,
        ): IsUndefinedCheckSourceTerm = IsUndefinedCheckSourceTerm(
            location = SourceLocation.build(ctx),
            argument = ExpressionSourceTerm.build(ctx),
        )
    }

    override fun dump(): String = "(isUndefined)"
}
