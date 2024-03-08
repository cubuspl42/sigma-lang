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
            val class_ = projectReference.resolveBuiltin(
                builtinName = Identifier.of(
                    when (variant) {
                        Variant.Strings -> "String"
                        Variant.Lists -> "List"
                    }
                )
            )

            val concatFunction = class_.call(IdentifierLiteral.of("concat"))

            concatFunction.call(
                passedArgument = UnorderedTupleConstructor(
                    valueByKey = mapOf(
                        Identifier(name = "left") to lazyOf(leftExpression),
                        Identifier(name = "right") to lazyOf(rightExpression),
                    ),
                ),
            )
        }
    }

    override fun wrap(): Value = UnorderedTuple(
        valueByKey = mapOf(
            Identifier.of("left") to lazyOf(left.wrap()),
            Identifier.of("right") to lazyOf(right.wrap()),
        ),
    )
}
