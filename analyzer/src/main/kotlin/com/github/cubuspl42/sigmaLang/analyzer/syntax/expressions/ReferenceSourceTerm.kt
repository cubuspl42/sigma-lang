package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.ReferenceContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier

data class ReferenceSourceTerm(
    override val location: SourceLocation,
    override val referredName: Identifier,
) : ExpressionSourceTerm(), ReferenceTerm {
    companion object {
        fun build(
            ctx: ReferenceContext,
        ): ReferenceSourceTerm = ReferenceSourceTerm(
            location = SourceLocation.build(ctx),
            referredName = Identifier(name = ctx.referee.text),
        )
    }

    override fun dump(): String = referredName.dump()
}
