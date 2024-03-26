package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub

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

    override fun transmute() = ExpressionStub.map2Nested(
        callee.transmute(),
        passedArgument.transmute(),
    ) { callee, passedArgument ->
        Call(
            callee = callee,
            passedArgument = passedArgument,
        )
    }

    override fun wrap(): Value = UnorderedTupleValue(
        valueByKey = mapOf(
            Identifier.of("callee") to lazyOf(callee.wrap()),
            Identifier.of("passedArgument") to lazyOf(passedArgument.wrap()),
        )
    )
}
