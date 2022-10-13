package sigma

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.AbstractionAltContext
import sigma.parser.antlr.SigmaParser.ApplicationAltContext
import sigma.parser.antlr.SigmaParser.LetExpressionAltContext
import sigma.parser.antlr.SigmaParser.ReferenceAltContext
import sigma.parser.antlr.SigmaParser.ScopeAltContext
import sigma.parser.antlr.SigmaParser.SymbolAltContext
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

            override fun visitReferenceAlt(
                ctx: ReferenceAltContext,
            ): Expression = Reference.build(ctx)

            override fun visitAbstractionAlt(
                ctx: AbstractionAltContext,
            ): Expression = Abstraction.build(ctx.abstraction())

            override fun visitApplicationAlt(
                ctx: ApplicationAltContext,
            ): Expression = Application.build(ctx)

            override fun visitLetExpressionAlt(
                ctx: LetExpressionAltContext,
            ): Expression = LetExpression.build(ctx.letExpression())

            override fun visitScopeAlt(
                ctx: ScopeAltContext,
            ): Expression = ScopeConstructor.build(ctx.scope())

            override fun visitSymbolAlt(
                ctx: SymbolAltContext,
            ): Expression = Symbol.of(ctx.symbol().identifier().text)
        }.visit(expression)
    }

    fun evaluate(
        scope: Scope = BuiltinScope,
    ): Value

    fun dump(): String
}
