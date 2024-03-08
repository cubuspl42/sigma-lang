package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import com.github.cubuspl42.sigmaLang.core.expressions.BooleanLiteral
import com.github.cubuspl42.sigmaLang.core.values.BooleanPrimitive
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.stubs.asStub

data class BooleanLiteralTerm(
    override val value: BooleanPrimitive,
) : PrimitiveLiteralTerm() {
    companion object : Term.Builder<SigmaParser.BooleanLiteralContext, BooleanLiteralTerm>() {
        val False = BooleanLiteralTerm(
            value = BooleanPrimitive.False,
        )

        val True = BooleanLiteralTerm(
            value = BooleanPrimitive.True,
        )

        override fun build(
            ctx: SigmaParser.BooleanLiteralContext,
        ): BooleanLiteralTerm = object : SigmaParserBaseVisitor<BooleanLiteralTerm>() {
            override fun visitFalseLiteral(ctx: SigmaParser.FalseLiteralContext?): BooleanLiteralTerm =
                BooleanLiteralTerm(value = BooleanPrimitive.False)

            override fun visitTrueLiteral(ctx: SigmaParser.TrueLiteralContext?): BooleanLiteralTerm =
                BooleanLiteralTerm(value = BooleanPrimitive.True)
        }.visit(ctx)

        override fun extract(
            parser: SigmaParser,
        ): SigmaParser.BooleanLiteralContext = parser.booleanLiteral()
    }

    override fun transmute() = BooleanLiteral(
        value = value,
    ).asStub()

    override fun wrap(): Value = UnorderedTuple(
        valueByKey = mapOf(
            Identifier.of("value") to lazyOf(value),
        )
    )
}
