package com.github.cubuspl42.sigmaLang.shell

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser

data class CallTerm(
    val callee: ReferenceTerm,
    val passedArgument: UnorderedTupleConstructorTerm,
) : ExpressionTerm {
    companion object {
        fun build(
            ctx: SigmaParser.CallContext,
        ): CallTerm = CallTerm(
            callee = ReferenceTerm.build(ctx.callee),
            passedArgument = UnorderedTupleConstructorTerm.build(ctx.passedArgument),
        )
    }
}
