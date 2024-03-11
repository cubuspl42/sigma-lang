package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.isA
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value
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

    override fun transmute(): ExpressionStub<ShadowExpression> = ExpressionStub.map2Unpacked(
        instance.transmute(),
        class_.transmute(),
    ) { instanceExpression, classExpression ->
        instanceExpression.isA(classExpression)
    }

    override fun wrap(): Value = UnorderedTuple(
        valueByKey = mapOf(
            Identifier.of("instance") to lazyOf(instance.wrap()),
            Identifier.of("class") to lazyOf(class_.wrap()),
        )
    )
}
