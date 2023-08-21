package sigma.syntax.expressions

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.AbstractionAltContext
import sigma.parser.antlr.SigmaParser.BinaryOperationAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionTupleConstructorAltContext
import sigma.parser.antlr.SigmaParser.CallableExpressionAltContext
import sigma.parser.antlr.SigmaParser.CallableParenAltContext
import sigma.parser.antlr.SigmaParser.CallableReferenceAltContext
import sigma.parser.antlr.SigmaParser.CallableTupleConstructorAltContext
import sigma.parser.antlr.SigmaParser.DictConstructorAltContext
import sigma.parser.antlr.SigmaParser.FieldReadAltContext
import sigma.parser.antlr.SigmaParser.IntLiteralAltContext
import sigma.parser.antlr.SigmaParser.IsUndefinedCheckAltContext
import sigma.parser.antlr.SigmaParser.LetExpressionAltContext
import sigma.parser.antlr.SigmaParser.ParenExpressionAltContext
import sigma.parser.antlr.SigmaParser.ReferenceAltContext
import sigma.parser.antlr.SigmaParser.SymbolLiteralAltContext
import sigma.parser.antlr.SigmaParser.TupleConstructorAltContext
import sigma.parser.antlr.SigmaParserBaseVisitor
import sigma.syntax.SourceTerm

sealed class ExpressionSourceTerm : SourceTerm() {
    companion object {
        fun parse(
            source: String,
        ): ExpressionSourceTerm {
            val sourceName = "__expression__"

            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)
            val parser = SigmaParser(tokenStream)

            return build(parser.expression())
        }

        fun build(
            ctx: ParserRuleContext,
        ): ExpressionSourceTerm = object : SigmaParserBaseVisitor<ExpressionSourceTerm>() {
            override fun visitBinaryOperationAlt(
                ctx: BinaryOperationAltContext,
            ): ExpressionSourceTerm = CallSourceTerm.build(ctx)

            override fun visitParenExpressionAlt(
                ctx: ParenExpressionAltContext,
            ): ExpressionSourceTerm = build(ctx.parenExpression().expression())

            override fun visitReferenceAlt(
                ctx: ReferenceAltContext,
            ): ExpressionSourceTerm = ReferenceSourceTerm.build(ctx.reference())

            override fun visitAbstractionAlt(
                ctx: AbstractionAltContext,
            ): ExpressionSourceTerm = AbstractionSourceTerm.build(ctx.abstraction())

            override fun visitTupleConstructorAlt(
                ctx: TupleConstructorAltContext,
            ): ExpressionSourceTerm = TupleConstructorSourceTerm.build(ctx.tupleConstructor())

            override fun visitDictConstructorAlt(
                ctx: DictConstructorAltContext,
            ): ExpressionSourceTerm = DictConstructorSourceTerm.build(ctx.dictConstructor())

            override fun visitLetExpressionAlt(
                ctx: LetExpressionAltContext,
            ): ExpressionSourceTerm = LetExpressionSourceTerm.build(ctx.letExpression())

            override fun visitIsUndefinedCheckAlt(
                ctx: IsUndefinedCheckAltContext,
            ): ExpressionSourceTerm = IsUndefinedCheckSourceTerm.build(ctx.isUndefinedCheck)

            override fun visitSymbolLiteralAlt(
                ctx: SymbolLiteralAltContext,
            ): ExpressionSourceTerm = SymbolLiteralSourceTerm.build(ctx)

            override fun visitIntLiteralAlt(
                ctx: IntLiteralAltContext,
            ): ExpressionSourceTerm = IntLiteralSourceTerm.build(ctx)

            override fun visitCallableExpressionAlt(
                ctx: CallableExpressionAltContext,
            ): ExpressionSourceTerm = build(ctx.callableExpression())

            override fun visitCallExpressionAlt(
                ctx: CallExpressionAltContext,
            ): ExpressionSourceTerm = CallSourceTerm.build(ctx)

            override fun visitCallExpressionTupleConstructorAlt(
                ctx: CallExpressionTupleConstructorAltContext,
            ): ExpressionSourceTerm = CallSourceTerm.build(ctx)

            override fun visitCallableParenAlt(
                ctx: CallableParenAltContext,
            ): ExpressionSourceTerm = build(ctx.parenExpression().expression())

            override fun visitCallableReferenceAlt(
                ctx: CallableReferenceAltContext,
            ): ExpressionSourceTerm = ReferenceSourceTerm.build(ctx.reference())

            override fun visitCallableTupleConstructorAlt(
                ctx: CallableTupleConstructorAltContext,
            ): ExpressionSourceTerm = TupleConstructorSourceTerm.build(ctx.tupleConstructor())

            override fun visitFieldReadAlt(
                ctx: FieldReadAltContext,
            ): ExpressionSourceTerm = FieldReadSourceTerm.build(ctx)

            override fun visitSetConstructor(
                ctx: SigmaParser.SetConstructorContext,
            ): ExpressionSourceTerm = SetConstructorSourceTerm.build(ctx)

            override fun visitIfExpressionAlt(
                ctx: SigmaParser.IfExpressionAltContext,
            ): ExpressionSourceTerm = IfExpressionSourceTerm.build(ctx.ifExpression())

            override fun visitTupleTypeConstructorAlt(
                ctx: SigmaParser.TupleTypeConstructorAltContext,
            ): ExpressionSourceTerm = TupleTypeConstructorSourceTerm.build(ctx.tupleTypeConstructor())

            override fun visitFunctionTypeConstructorAlt(
                ctx: SigmaParser.FunctionTypeConstructorAltContext,
            ): ExpressionSourceTerm = FunctionTypeConstructorSourceTerm.build(ctx.functionTypeConstructor())

            override fun visitArrayTypeConstructorAlt(
                ctx: SigmaParser.ArrayTypeConstructorAltContext,
            ): ExpressionSourceTerm = ArrayTypeConstructorSourceTerm.build(ctx.arrayTypeConstructor())

            override fun visitDictTypeConstructorAlt(
                ctx: SigmaParser.DictTypeConstructorAltContext,
            ): ExpressionSourceTerm = DictTypeConstructorSourceTerm.build(ctx.dictTypeConstructor())
        }.visit(ctx) ?: throw IllegalArgumentException("Can't match expression ${ctx::class.java}")
    }

    abstract fun dump(): String
}
