package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import com.github.cubuspl42.sigmaLang.core.expressions.BooleanLiteral
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.BooleanPrimitive
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.stubs.asStub

data class BooleanLiteralTerm(
    private val value: BooleanPrimitive,
) : ExpressionTerm {
    companion object : Term.Builder<SigmaParser.BooleanLiteralContext, BooleanLiteralTerm>() {
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
}
