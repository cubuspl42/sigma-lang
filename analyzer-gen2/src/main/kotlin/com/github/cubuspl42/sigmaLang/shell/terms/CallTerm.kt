package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.stubs.CallStub
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub

data class CallTerm(
    val callee: ReferenceTerm,
    val passedArgument: UnorderedTupleConstructorTerm,
) : ExpressionTerm {
    companion object : Term.Builder<SigmaParser.CallContext, CallTerm>() {
        override fun build(
            ctx: SigmaParser.CallContext,
        ): CallTerm = CallTerm(
            callee = ReferenceTerm.build(ctx.reference()),
            passedArgument = UnorderedTupleConstructorTerm.build(ctx.unorderedTupleConstructor()),
        )

        override fun extract(parser: SigmaParser): SigmaParser.CallContext = parser.call()
    }

    override fun transmute(): ExpressionStub<*> = CallStub(
        calleeStub = callee.transmute(),
        passedArgumentStub = passedArgument.transmute(),
    )
}
