package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.BuiltinModuleReference
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.TransmutationContext

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

    override fun transmute(context: TransmutationContext): Expression {
        val isA = BuiltinModuleReference.isAFunction

        return isA.call(
            instance = instance.transmute(context = context),
            class_ = class_.transmute(context = context),
        )
    }

    override fun wrap(): Value = UnorderedTupleValue(
        valueByKey = mapOf(
            Identifier.of("instance") to lazyOf(instance.wrap()),
            Identifier.of("class") to lazyOf(class_.wrap()),
        )
    )
}
