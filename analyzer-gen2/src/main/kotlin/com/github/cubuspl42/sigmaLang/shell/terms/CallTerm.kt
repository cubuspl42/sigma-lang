package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.stubs.CallStub
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub

data class CallTerm(
    val callee: ExpressionTerm,
    val passedArgument: UnorderedTupleConstructorTerm,
) : ExpressionTerm {
    companion object {
        fun build(
            ctx: SigmaParser.CallCallableExpressionAltContext,
        ): CallTerm = CallTerm(
            callee = ExpressionTerm.build(ctx.callee()),
            passedArgument = UnorderedTupleConstructorTerm.build(ctx.unorderedTupleConstructor()),
        )
    }

    override fun transmute(): ExpressionStub<ShadowExpression> = CallStub(
        calleeStub = callee.transmute(),
        passedArgumentStub = passedArgument.transmute(),
    )
}
