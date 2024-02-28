package com.github.cubuspl42.sigmaLang.shell

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser

data class ReferenceTerm(
    val referredName: IdentifierTerm,
) : ExpressionTerm {
    companion object {
        fun build(
            ctx: SigmaParser.ReferenceContext,
        ): ReferenceTerm = ReferenceTerm(
            referredName = IdentifierTerm.build(ctx.referredName),
        )
    }
}
