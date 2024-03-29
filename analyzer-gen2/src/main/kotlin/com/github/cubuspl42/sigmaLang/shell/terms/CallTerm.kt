package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.TransmutationContext

data class CallTerm(
    val callee: ExpressionTerm,
    val passedArgument: TupleConstructorTerm,
) : ExpressionTerm {
    companion object {
        fun build(
            ctx: SigmaParser.CallCallableExpressionAltContext,
        ): CallTerm = CallTerm(
            callee = ExpressionTerm.build(ctx.callee()),
            passedArgument = TupleConstructorTerm.build(ctx.tupleConstructor()),
        )
    }

    override fun transmute(
        context: TransmutationContext,
    ): Expression = Call(
        callee = callee.transmute(context),
        passedArgument = passedArgument.transmute(context),
    )

    override fun wrap(): Value = UnorderedTupleValue(
        valueByKey = mapOf(
            Identifier.of("callee") to lazyOf(callee.wrap()),
            Identifier.of("passedArgument") to lazyOf(passedArgument.wrap()),
        )
    )
}
