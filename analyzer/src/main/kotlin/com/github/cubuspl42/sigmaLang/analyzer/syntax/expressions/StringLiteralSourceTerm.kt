package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.StringValue
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.StringLiteralAltContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class StringLiteralSourceTerm(
    override val location: SourceLocation,
    override val value: StringValue,
) : ExpressionSourceTerm(), StringLiteralTerm {
    companion object {
        fun build(
            ctx: StringLiteralAltContext,
        ): StringLiteralSourceTerm {
            val content = ctx.text.drop(1).dropLast(1)

            return StringLiteralSourceTerm(
                location = SourceLocation.build(ctx),
                value = StringValue(value = content),
            )
        }
    }

    override fun dump(): String = value.toString()
}
