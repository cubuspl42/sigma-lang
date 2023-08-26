package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaLexer
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.AbstractionAltContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.BinaryOperationAltContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.CallExpressionAltContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.CallExpressionTupleConstructorAltContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.CallableExpressionAltContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.CallableParenAltContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.CallableReferenceAltContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.CallableTupleConstructorAltContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.DictConstructorAltContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.FieldReadAltContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.IntLiteralAltContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.IsUndefinedCheckAltContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.LetExpressionAltContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.ParenExpressionAltContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.ReferenceAltContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.TupleConstructorAltContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceTerm

sealed class ExpressionSourceTerm : SourceTerm() {
    companion object {
        fun parse(
            source: String,
        ): ExpressionTerm {
            val sourceName = "__expression__"

            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)
            val parser = SigmaParser(tokenStream)

            return build(parser.expression())
        }

        fun build(
            ctx: ParserRuleContext,
        ): ExpressionTerm = object : SigmaParserBaseVisitor<ExpressionTerm>() {
            override fun visitBinaryOperationAlt(
                ctx: BinaryOperationAltContext,
            ): ExpressionTerm = InfixCallSourceTerm.build(ctx)

            override fun visitParenExpressionAlt(
                ctx: ParenExpressionAltContext,
            ): ExpressionTerm = build(ctx.parenExpression().expression())

            override fun visitReferenceAlt(
                ctx: ReferenceAltContext,
            ): ExpressionTerm = ReferenceSourceTerm.build(ctx.reference())

            override fun visitAbstractionAlt(
                ctx: AbstractionAltContext,
            ): ExpressionTerm = AbstractionSourceTerm.build(ctx.abstraction())

            override fun visitTupleConstructorAlt(
                ctx: TupleConstructorAltContext,
            ): ExpressionTerm = TupleConstructorSourceTerm.build(ctx.tupleConstructor())

            override fun visitDictConstructorAlt(
                ctx: DictConstructorAltContext,
            ): ExpressionTerm = DictConstructorSourceTerm.build(ctx.dictConstructor())

            override fun visitLetExpressionAlt(
                ctx: LetExpressionAltContext,
            ): ExpressionTerm = LetExpressionSourceTerm.build(ctx.letExpression())

            override fun visitIsUndefinedCheckAlt(
                ctx: IsUndefinedCheckAltContext,
            ): ExpressionTerm = IsUndefinedCheckSourceTerm.build(ctx.isUndefinedCheck)

            override fun visitIntLiteralAlt(
                ctx: IntLiteralAltContext,
            ): ExpressionTerm = IntLiteralSourceTerm.build(ctx)

            override fun visitCallableExpressionAlt(
                ctx: CallableExpressionAltContext,
            ): ExpressionTerm = build(ctx.callableExpression())

            override fun visitCallExpressionAlt(
                ctx: CallExpressionAltContext,
            ): ExpressionTerm = PostfixCallSourceTerm.build(ctx)

            override fun visitCallExpressionTupleConstructorAlt(
                ctx: CallExpressionTupleConstructorAltContext,
            ): ExpressionTerm = PostfixCallSourceTerm.build(ctx)

            override fun visitCallableParenAlt(
                ctx: CallableParenAltContext,
            ): ExpressionTerm = build(ctx.parenExpression().expression())

            override fun visitCallableReferenceAlt(
                ctx: CallableReferenceAltContext,
            ): ExpressionTerm = ReferenceSourceTerm.build(ctx.reference())

            override fun visitCallableTupleConstructorAlt(
                ctx: CallableTupleConstructorAltContext,
            ): ExpressionTerm = TupleConstructorSourceTerm.build(ctx.tupleConstructor())

            override fun visitFieldReadAlt(
                ctx: FieldReadAltContext,
            ): ExpressionTerm = FieldReadSourceTerm.build(ctx)

            override fun visitSetConstructor(
                ctx: SigmaParser.SetConstructorContext,
            ): ExpressionTerm = SetConstructorSourceTerm.build(ctx)

            override fun visitIfExpressionAlt(
                ctx: SigmaParser.IfExpressionAltContext,
            ): ExpressionTerm = IfExpressionSourceTerm.build(ctx.ifExpression())

            override fun visitTupleTypeConstructorAlt(
                ctx: SigmaParser.TupleTypeConstructorAltContext,
            ): ExpressionTerm = TupleTypeConstructorSourceTerm.build(ctx.tupleTypeConstructor())

            override fun visitFunctionTypeConstructorAlt(
                ctx: SigmaParser.FunctionTypeConstructorAltContext,
            ): ExpressionTerm = FunctionTypeConstructorSourceTerm.build(ctx.functionTypeConstructor())

            override fun visitArrayTypeConstructorAlt(
                ctx: SigmaParser.ArrayTypeConstructorAltContext,
            ): ExpressionTerm = ArrayTypeConstructorSourceTerm.build(ctx.arrayTypeConstructor())

            override fun visitDictTypeConstructorAlt(
                ctx: SigmaParser.DictTypeConstructorAltContext,
            ): ExpressionTerm = DictTypeConstructorSourceTerm.build(ctx.dictTypeConstructor())
        }.visit(ctx) ?: throw IllegalArgumentException("Can't match expression ${ctx::class.java}")
    }
}
