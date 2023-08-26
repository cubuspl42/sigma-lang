package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.SetConstructorContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class SetConstructorSourceTerm(
    override val location: SourceLocation,
    override val elements: List<ExpressionTerm>,
) : ExpressionSourceTerm(), SetConstructorTerm {
    companion object {
        fun build(
            ctx: SetConstructorContext,
        ): SetConstructorSourceTerm = SetConstructorSourceTerm(
            location = SourceLocation.build(ctx),
            elements = ctx.elements.map {
                ExpressionSourceTerm.build(it)
            },
        )
    }

    override fun dump(): String = "(set constructor)"
}
