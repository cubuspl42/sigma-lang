package sigma

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.AbstractionAltContext
import sigma.parser.antlr.SigmaParser.ApplicationAltContext
import sigma.parser.antlr.SigmaParser.ReferenceAltContext
import sigma.parser.antlr.SigmaParser.SymbolAltContext
import sigma.parser.antlr.SigmaParser.TableAltContext
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
            override fun visitTableAlt(
                ctx: TableAltContext,
            ): Expression = TableExpression.build(ctx.table())

            override fun visitAbstractionAlt(
                ctx: AbstractionAltContext,
            ): Expression = Abstraction.build(ctx.abstraction())

            override fun visitReferenceAlt(
                ctx: ReferenceAltContext,
            ): Expression = Reference.build(ctx)

            override fun visitApplicationAlt(
                ctx: ApplicationAltContext,
            ): Expression = Application.build(ctx)

            override fun visitSymbolAlt(
                ctx: SymbolAltContext,
            ): Expression = Symbol.build(ctx.symbol())
        }.visit(expression)
    }

    fun evaluate(
        scope: Scope = Scope.Empty,
    ): Value

    fun dump(): String
}
