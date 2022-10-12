package sigma

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.ReadAltContext
import sigma.parser.antlr.SigmaParser.ReferenceAltContext
import sigma.parser.antlr.SigmaParser.ValueAltContext
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
            override fun visitValueAlt(
                ctx: ValueAltContext,
            ): Expression = Value.build(ctx.value())

            override fun visitReferenceAlt(
                ctx: ReferenceAltContext,
            ): Expression = Reference.build(ctx)

            override fun visitReadAlt(
                ctx: ReadAltContext,
            ): Expression = Application.build(ctx)
        }.visit(expression)
    }

    fun evaluate(
        scope: Scope = Scope.Empty,
    ): Value

    fun dump(): String
}
