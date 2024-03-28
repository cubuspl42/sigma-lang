package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaLexer
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.BuiltinModuleReference
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.TransmutationContext

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

    override fun transmute(context: TransmutationContext): Expression {
        val leftExpression = left.transmute(context = context)
        val rightExpression = right.transmute(context = context)

        return when (variant) {
            Variant.Strings -> BuiltinModuleReference.stringClass.concat.call(
                string = leftExpression,
                otherString = rightExpression,
            )

            Variant.Lists -> BuiltinModuleReference.listClass.concat.call(
                list = leftExpression,
                otherList = rightExpression,
            )
        }
    }

    override fun wrap(): Value = UnorderedTupleValue(
        valueByKey = mapOf(
            Identifier.of("left") to lazyOf(left.wrap()),
            Identifier.of("right") to lazyOf(right.wrap()),
        ),
    )
}
