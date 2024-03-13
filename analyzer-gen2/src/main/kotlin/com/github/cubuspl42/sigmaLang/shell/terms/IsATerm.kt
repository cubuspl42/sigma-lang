package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub

data class IsATerm(
    val instance: ExpressionTerm,
    @Suppress("PropertyName") val class_: ExpressionTerm,
) : ExpressionTerm {
    companion object {
        fun build(
            ctx: SigmaParser.IsAExpressionAltContext,
        ): IsATerm = IsATerm(
            instance = ExpressionTerm.build(ctx.instance),
            class_ = ExpressionTerm.build(ctx.class_),
        )
    }

    override fun transmute(): ExpressionStub<Call> = object : ExpressionStub<Call>() {
        override fun transform(
            context: FormationContext,
        ) = object : ExpressionBuilder<Call>() {
            override fun build(
                buildContext: Expression.BuildContext,
            ): Call {
                val isA = buildContext.builtinModule.isAFunction

                return isA.call(
                    instance = instance.build(
                        formationContext = context,
                        buildContext = buildContext,
                    ),
                    class_ = class_.build(
                        formationContext = context,
                        buildContext = buildContext,
                    ),
                )
            }
        }
    }

    override fun wrap(): Value = UnorderedTuple(
        valueByKey = mapOf(
            Identifier.of("instance") to lazyOf(instance.wrap()),
            Identifier.of("class") to lazyOf(class_.wrap()),
        )
    )
}
