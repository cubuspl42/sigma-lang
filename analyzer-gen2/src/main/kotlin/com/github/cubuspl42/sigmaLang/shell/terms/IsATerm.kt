package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.BuiltinModuleReference
import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.TransmutationContext
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
            context: TransmutationContext,
        ): Call {
            val isA = BuiltinModuleReference.isAFunction

            return isA.call(
                instance = instance.transmuteFully(
                    context = context,
                ),
                class_ = class_.transmuteFully(
                    context = context,
                ),
            )
        }
    }


    override fun wrap(): Value = UnorderedTupleValue(
        valueByKey = mapOf(
            Identifier.of("instance") to lazyOf(instance.wrap()),
            Identifier.of("class") to lazyOf(class_.wrap()),
        )
    )
}
