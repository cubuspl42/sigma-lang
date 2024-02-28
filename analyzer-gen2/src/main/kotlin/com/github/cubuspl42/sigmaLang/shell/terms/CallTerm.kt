package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.shell.ConstructionContext

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

    override fun construct(context: ConstructionContext): Lazy<Expression> = lazy {
        Call(
            calleeLazy = callee.construct(context),
            passedArgumentLazy = passedArgument.construct(context),
        )
    }
}
