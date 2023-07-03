package sigma.syntax.expressions

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import sigma.BuiltinScope
import sigma.TypeScope
import sigma.SyntaxValueScope
import sigma.Thunk
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
import sigma.syntax.Term
import sigma.evaluation.values.Value
import sigma.evaluation.scope.Scope

sealed class ExpressionTerm : Term() {
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
            ): ExpressionTerm = CallTerm.build(ctx)

            override fun visitParenExpressionAlt(
                ctx: ParenExpressionAltContext,
            ): ExpressionTerm = build(ctx.parenExpression().expression())

            override fun visitReferenceAlt(
                ctx: ReferenceAltContext,
            ): ExpressionTerm = ReferenceTerm.build(ctx.reference())

            override fun visitAbstractionAlt(
                ctx: AbstractionAltContext,
            ): ExpressionTerm = AbstractionTerm.build(ctx.abstraction())

            override fun visitTupleConstructorAlt(
                ctx: TupleConstructorAltContext,
            ): ExpressionTerm = TupleConstructorTerm.build(ctx.tupleConstructor())

            override fun visitDictConstructorAlt(
                ctx: DictConstructorAltContext,
            ): ExpressionTerm = DictConstructorTerm.build(ctx.dictConstructor())

            override fun visitLetExpressionAlt(
                ctx: LetExpressionAltContext,
            ): ExpressionTerm = LetExpressionTerm.build(ctx.letExpression())

            override fun visitIsUndefinedCheckAlt(
                ctx: IsUndefinedCheckAltContext,
            ): ExpressionTerm = IsUndefinedCheckTerm.build(ctx.isUndefinedCheck)

            override fun visitSymbolLiteralAlt(
                ctx: SymbolLiteralAltContext,
            ): ExpressionTerm = SymbolLiteralTerm.build(ctx)

            override fun visitIntLiteralAlt(
                ctx: IntLiteralAltContext,
            ): ExpressionTerm = IntLiteralTerm.build(ctx)

            override fun visitCallableExpressionAlt(
                ctx: CallableExpressionAltContext,
            ): ExpressionTerm = build(ctx.callableExpression())

            override fun visitCallExpressionAlt(
                ctx: CallExpressionAltContext,
            ): ExpressionTerm = CallTerm.build(ctx)

            override fun visitCallExpressionTupleConstructorAlt(
                ctx: CallExpressionTupleConstructorAltContext,
            ): ExpressionTerm = CallTerm.build(ctx)

            override fun visitCallableParenAlt(
                ctx: CallableParenAltContext,
            ): ExpressionTerm = build(ctx.parenExpression().expression())

            override fun visitCallableReferenceAlt(
                ctx: CallableReferenceAltContext,
            ): ExpressionTerm = ReferenceTerm.build(ctx.reference())

            override fun visitCallableTupleConstructorAlt(
                ctx: CallableTupleConstructorAltContext,
            ): ExpressionTerm = TupleConstructorTerm.build(ctx.tupleConstructor())

            override fun visitFieldReadAlt(
                ctx: FieldReadAltContext,
            ): ExpressionTerm = FieldReadTerm.build(ctx)

            override fun visitSetConstructor(
                ctx: SigmaParser.SetConstructorContext,
            ): ExpressionTerm = SetConstructorTerm.build(ctx)
        }.visit(ctx) ?: throw IllegalArgumentException("Can't match expression ${ctx::class}")
    }

    inner class BoundThunk(private val scope: Scope) : Thunk() {
        override val toEvaluatedValue: Value by lazy {
            this@ExpressionTerm.evaluate(
                scope = scope,
            ).toEvaluatedValue
        }

        override fun dump(): String = "(bound thunk)"
    }

    // Isn't this the same as `evaluate`?
    fun bind(scope: Scope): Thunk = BoundThunk(scope = scope)

    fun evaluateAsRoot(): Value = evaluate(scope = BuiltinScope).toEvaluatedValue

    // Idea for naming:
    // "environment" - scope in which an expression is evaluated
    // "context" - outer scope for nested scopes
    abstract fun evaluate(
        scope: Scope,
    ): Thunk

    abstract fun dump(): String
}
