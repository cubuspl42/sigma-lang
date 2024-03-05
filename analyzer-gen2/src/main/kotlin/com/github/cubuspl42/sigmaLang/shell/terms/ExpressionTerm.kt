package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaLexer
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.shell.ConstructionContext
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
            ctx: SigmaParser.ExpressionContext,
        ): ExpressionTerm = object : SigmaParserBaseVisitor<ExpressionTerm>() {
            override fun visitReferenceExpressionAlt(
                ctx: SigmaParser.ReferenceExpressionAltContext,
            ): ExpressionTerm = ReferenceTerm.build(ctx.reference())

            override fun visitCallExpressionAlt(
                ctx: SigmaParser.CallExpressionAltContext,
            ): ExpressionTerm = CallTerm.build(ctx.call())

            override fun visitFieldReadExpressionAlt(
                ctx: SigmaParser.FieldReadExpressionAltContext,
            ): ExpressionTerm = FieldReadTerm.build(ctx.fieldRead())

            override fun visitUnorderedTupleConstructorExpressionAlt(
                ctx: SigmaParser.UnorderedTupleConstructorExpressionAltContext,
            ): ExpressionTerm = UnorderedTupleConstructorTerm.build(ctx.unorderedTupleConstructor())

            override fun visitAbstractionConstructorExpressionAlt(
                ctx: SigmaParser.AbstractionConstructorExpressionAltContext,
            ): ExpressionTerm = AbstractionConstructorTerm.build(ctx.abstractionConstructor())

            override fun visitBooleanLiteralExpressionAlt(
                ctx: SigmaParser.BooleanLiteralExpressionAltContext,
            ): ExpressionTerm = BooleanLiteralTerm.build(ctx.booleanLiteral())

            override fun visitLetInExpressionAlt(
                ctx: SigmaParser.LetInExpressionAltContext,
            ): ExpressionTerm = LetInTerm.build(ctx.letIn())

            override fun visitWhenExpressionAlt(
                ctx: SigmaParser.WhenExpressionAltContext,
            ): ExpressionTerm = WhenTerm.build(ctx.`when`())
        }.visit(ctx)
    }

    fun construct(context: ConstructionContext): Lazy<Expression>
}
