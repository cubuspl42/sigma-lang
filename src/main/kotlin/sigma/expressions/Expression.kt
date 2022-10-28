package sigma.expressions

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import sigma.GlobalContext
import sigma.Thunk
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.AbstractionAltContext
import sigma.parser.antlr.SigmaParser.BinaryOperationAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionDictAltContext
import sigma.parser.antlr.SigmaParser.CallableDictAltContext
import sigma.parser.antlr.SigmaParser.CallableExpressionAltContext
import sigma.parser.antlr.SigmaParser.CallableParenAltContext
import sigma.parser.antlr.SigmaParser.CallableReferenceAltContext
import sigma.parser.antlr.SigmaParser.DictAltContext
import sigma.parser.antlr.SigmaParser.LetExpressionAltContext
import sigma.parser.antlr.SigmaParser.ParenExpressionAltContext
import sigma.parser.antlr.SigmaParser.ReferenceAltContext
import sigma.parser.antlr.SigmaParser.SymbolAltContext
import sigma.parser.antlr.SigmaParserBaseVisitor
import sigma.types.Type
import sigma.values.tables.Scope

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
            expression: ParserRuleContext,
        ): Expression = object : SigmaParserBaseVisitor<Expression>() {
            override fun visitBinaryOperationAlt(
                ctx: BinaryOperationAltContext,
            ): Expression = Application.build(ctx)

            override fun visitParenExpressionAlt(
                ctx: ParenExpressionAltContext,
            ): Expression = build(ctx.parenExpression().expression())

            override fun visitReferenceAlt(
                ctx: ReferenceAltContext,
            ): Expression = Reference.build(ctx.reference())

            override fun visitAbstractionAlt(
                ctx: AbstractionAltContext,
            ): Expression = Abstraction.build(ctx.abstraction())

            override fun visitDictAlt(
                ctx: DictAltContext,
            ): Expression = DictConstructor.build(ctx.dict())

            override fun visitLetExpressionAlt(
                ctx: LetExpressionAltContext,
            ): Expression = LetExpression.build(ctx.letExpression())

            override fun visitSymbolAlt(
                ctx: SymbolAltContext,
            ): Expression = SymbolLiteral.build(ctx.symbol().identifier())

            override fun visitCallableExpressionAlt(
                ctx: CallableExpressionAltContext,
            ): Expression = build(ctx.callableExpression())

            override fun visitCallExpressionAlt(
                ctx: CallExpressionAltContext,
            ): Expression = Application.build(ctx)

            override fun visitCallExpressionDictAlt(
                ctx: CallExpressionDictAltContext,
            ): Expression = Application.build(ctx)

            override fun visitCallableParenAlt(
                ctx: CallableParenAltContext,
            ): Expression = build(ctx.parenExpression().expression())

            override fun visitCallableReferenceAlt(
                ctx: CallableReferenceAltContext,
            ): Expression = Reference.build(ctx.reference())

            override fun visitCallableDictAlt(
                ctx: CallableDictAltContext,
            ): Expression = DictConstructor.build(ctx.dict())
        }.visit(expression) ?: throw IllegalArgumentException("Can't match expression ${expression::class}")
    }

    fun inferType(): Type

    // Thought: Should `context` be `environment`?
    fun evaluate(
        context: Scope = GlobalContext,
    ): Thunk

    fun dump(): String
}
