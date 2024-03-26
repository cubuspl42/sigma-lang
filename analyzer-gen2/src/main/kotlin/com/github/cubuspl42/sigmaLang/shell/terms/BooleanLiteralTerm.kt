package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import com.github.cubuspl42.sigmaLang.core.expressions.BooleanLiteral
import com.github.cubuspl42.sigmaLang.core.values.BooleanValue
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub
import com.github.cubuspl42.sigmaLang.shell.stubs.asStub

data class BooleanLiteralTerm(
    override val value: BooleanValue,
) : PrimitiveLiteralTerm() {
    companion object : Term.Builder<SigmaParser.BooleanLiteralContext, BooleanLiteralTerm>() {
        val False = BooleanLiteralTerm(
            value = BooleanValue.False,
        )

        val True = BooleanLiteralTerm(
            value = BooleanValue.True,
        )

        override fun build(
            ctx: SigmaParser.BooleanLiteralContext,
        ): BooleanLiteralTerm = object : SigmaParserBaseVisitor<BooleanLiteralTerm>() {
            override fun visitFalseLiteral(ctx: SigmaParser.FalseLiteralContext?): BooleanLiteralTerm =
                BooleanLiteralTerm(value = BooleanValue.False)

            override fun visitTrueLiteral(ctx: SigmaParser.TrueLiteralContext?): BooleanLiteralTerm =
                BooleanLiteralTerm(value = BooleanValue.True)
        }.visit(ctx)

        override fun extract(
            parser: SigmaParser,
        ): SigmaParser.BooleanLiteralContext = parser.booleanLiteral()
    }

    override fun transmute() = BooleanLiteral(
        value = value,
    ).asStub()

    override fun wrap(): Value = UnorderedTupleValue(
        valueByKey = mapOf(
            Identifier.of("value") to lazyOf(value),
        )
    )
}
