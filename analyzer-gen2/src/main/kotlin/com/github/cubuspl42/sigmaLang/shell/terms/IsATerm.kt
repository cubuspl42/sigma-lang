package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.map
import com.github.cubuspl42.sigmaLang.core.values.Identifier
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
        ExpressionBuilder.isAFunction.map { isAFunction ->
            isAFunction.rawExpression.call(
                passedArgument = UnorderedTupleConstructor(
                    valueByKey = mapOf(
                        Identifier(name = "instance") to lazyOf(instanceExpression),
                        Identifier(name = "class") to lazyOf(classExpression),
                    ),
                ),
            )
        }
    }
}
