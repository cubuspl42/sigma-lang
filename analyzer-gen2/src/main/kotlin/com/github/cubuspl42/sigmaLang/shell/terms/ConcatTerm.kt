package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaLexer
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.call
import com.github.cubuspl42.sigmaLang.core.expressions.IdentifierLiteral
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.map
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub

data class ConcatTerm(
    val left: ExpressionTerm,
    val right: ExpressionTerm,
    val variant: Variant,
) : ExpressionTerm {
    enum class Variant {
        Strings, Lists,
    }

    companion object {
        fun build(
            ctx: SigmaParser.ConcatExpressionAltContext,
        ): ConcatTerm = ConcatTerm(
            left = ExpressionTerm.build(ctx.left),
            right = ExpressionTerm.build(ctx.right),
            variant = when (val type = ctx.variant.type) {
                SigmaLexer.ConcatStringsOperator -> Variant.Strings
                SigmaParser.ConcatListsOperator -> Variant.Lists
                else -> throw IllegalStateException("Unknown variant: $type")
            },
        )
    }

    override fun transmute(): ExpressionStub<ShadowExpression> = ExpressionStub.map2Unpacked(
        left.transmute(),
        right.transmute(),
    ) { leftExpression, rightExpression ->
        ExpressionBuilder.projectReference.map { projectReference ->
            val builtinModule = projectReference.builtinModule

            return@map when (variant) {
                Variant.Strings -> builtinModule.stringClass.concat.call(
                    string = leftExpression,
                    otherString = rightExpression,
                )

                Variant.Lists -> builtinModule.listClass.concat.call(
                    list = leftExpression,
                    otherList = rightExpression,
                )
            }
        }
    }

    override fun wrap(): Value = UnorderedTuple(
        valueByKey = mapOf(
            Identifier.of("left") to lazyOf(left.wrap()),
            Identifier.of("right") to lazyOf(right.wrap()),
        ),
    )
}
