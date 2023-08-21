package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.ReferenceContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol

data class ReferenceSourceTerm(
    override val location: SourceLocation,
    override val referredName: Symbol,
) : ExpressionSourceTerm(), ReferenceTerm {
    companion object {
        fun build(
            ctx: ReferenceContext,
        ): ReferenceSourceTerm = ReferenceSourceTerm(
            location = SourceLocation.build(ctx),
            referredName = Symbol(name = ctx.referee.text),
        )
    }

    override fun dump(): String = referredName.dump()
}
