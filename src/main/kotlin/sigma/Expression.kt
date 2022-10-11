package sigma

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.FormAltContext
import sigma.parser.antlr.SigmaParser.FormContext
import sigma.parser.antlr.SigmaParser.IdentifierAltContext
import sigma.parser.antlr.SigmaParser.IdentifierContext
import sigma.parser.antlr.SigmaParser.ReadAltContext
import sigma.parser.antlr.SigmaParserBaseVisitor

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
                ctx: FormAltContext,
            ): Expression = FormExpression.build(ctx.form())

            override fun visitIdentifierAlt(
                ctx: IdentifierAltContext,
            ): Expression = IdentifierExpression.build(ctx.identifier())

            override fun visitReadAlt(
                ctx: ReadAltContext,
            ): Expression {
                return ReadExpression.build(ctx)
            }
        }.visit(expression)
    }

    fun evaluate(): Value

    fun dump(): String
}
