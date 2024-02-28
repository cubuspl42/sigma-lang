package com.github.cubuspl42.sigmaLang.shell

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaLexer
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

sealed interface ExpressionTerm : Term {
    companion object {
        fun parse(
            source: String,
        ): ExpressionTerm {
            val sourceName = "__main__"

            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)
            val parser = SigmaParser(tokenStream)

            return build(parser.expression())
        }

        fun build(
            context: SigmaParser.ExpressionContext,
        ): ExpressionTerm = object : SigmaParserBaseVisitor<ExpressionTerm>() {
            override fun visitUnorderedTupleConstructorExpressionAlt(
                ctx: SigmaParser.UnorderedTupleConstructorExpressionAltContext,
            ): ExpressionTerm {
                return UnorderedTupleConstructorTerm.build(ctx.unorderedTupleConstructor())
            }
        }.visit(context)
    }
}
