package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.IntLiteralAltContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class IntLiteralSourceTerm(
    override val location: SourceLocation,
    override val value: IntValue,
) : ExpressionSourceTerm(), IntLiteralTerm {
    companion object {
        fun build(
            ctx: IntLiteralAltContext,
        ): IntLiteralSourceTerm = IntLiteralSourceTerm(
            location = SourceLocation.build(ctx),
            value = IntValue(value = ctx.text.toLong()),
        )
    }

    override fun dump(): String = value.toString()
}
