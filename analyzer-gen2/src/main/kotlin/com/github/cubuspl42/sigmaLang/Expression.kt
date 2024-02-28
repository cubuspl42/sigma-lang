package com.github.cubuspl42.sigmaLang

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaLexer
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

sealed interface Expression {
    companion object {
        fun parse(
            source: String,
        ): Expression {
            val sourceName = "__main__"

            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)
            val parser = SigmaParser(tokenStream)

            return build(parser.expression())
        }

        fun build(
            expression: SigmaParser.ExpressionContext,
        ): Expression = object : SigmaParserBaseVisitor<Expression>() {
            override fun visitFormAlt(
                ctx: SigmaParser.FormAltContext,
            ): Expression = FormExpression.build(ctx.form())
        }.visit(expression)
    }

    fun dump(): String
}
