package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.ArgumentReference
import com.github.cubuspl42.sigmaLang.core.expressions.KnotReference
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.scope.StaticScope
import com.github.cubuspl42.sigmaLang.shell.stubs.CallStub
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub
import com.github.cubuspl42.sigmaLang.shell.stubs.ReferenceStub
import com.github.cubuspl42.sigmaLang.shell.stubs.asStub

data class ReferenceTerm(
    val referredName: IdentifierTerm,
) : ExpressionTerm {
    companion object : Term.Builder<SigmaParser.ReferenceContext, ReferenceTerm>() {
        override fun build(
            ctx: SigmaParser.ReferenceContext,
        ): ReferenceTerm = ReferenceTerm(
            referredName = IdentifierTerm.build(ctx.referredName),
        )

        override fun extract(parser: SigmaParser): SigmaParser.ReferenceContext = parser.reference()
    }

    override fun transmute(): ExpressionStub<ShadowExpression> = ReferenceStub(
        referredName = referredName.transmute(),
    )
}
