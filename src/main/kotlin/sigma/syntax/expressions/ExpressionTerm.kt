package sigma.syntax.expressions

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import sigma.BuiltinScope
import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.Thunk
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.AbstractionAltContext
import sigma.parser.antlr.SigmaParser.BinaryOperationAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionAltContext
import sigma.parser.antlr.SigmaParser.CallExpressionTupleLiteralAltContext
import sigma.parser.antlr.SigmaParser.CallableExpressionAltContext
import sigma.parser.antlr.SigmaParser.CallableParenAltContext
import sigma.parser.antlr.SigmaParser.CallableReferenceAltContext
import sigma.parser.antlr.SigmaParser.CallableTupleLiteralAltContext
import sigma.parser.antlr.SigmaParser.DictLiteralAltContext
import sigma.parser.antlr.SigmaParser.FieldReadAltContext
import sigma.parser.antlr.SigmaParser.IntLiteralAltContext
import sigma.parser.antlr.SigmaParser.IsUndefinedCheckAltContext
import sigma.parser.antlr.SigmaParser.LetExpressionAltContext
import sigma.parser.antlr.SigmaParser.ParenExpressionAltContext
import sigma.parser.antlr.SigmaParser.ReferenceAltContext
import sigma.parser.antlr.SigmaParser.SymbolLiteralAltContext
import sigma.parser.antlr.SigmaParser.TupleLiteralAltContext
import sigma.parser.antlr.SigmaParserBaseVisitor
import sigma.semantics.types.Type
import sigma.syntax.Term
import sigma.values.Value
import sigma.values.tables.Scope

sealed class ExpressionTerm : Term() {
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

            override fun visitTupleLiteralAlt(
                ctx: TupleLiteralAltContext,
            ): ExpressionTerm = TupleLiteralTerm.build(ctx.tupleLiteral())

            override fun visitDictLiteralAlt(
                ctx: DictLiteralAltContext,
            ): ExpressionTerm = DictLiteralTerm.build(ctx.dictLiteral())

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

            override fun visitCallExpressionTupleLiteralAlt(
                ctx: CallExpressionTupleLiteralAltContext,
            ): ExpressionTerm = CallTerm.build(ctx)

            override fun visitCallableParenAlt(
                ctx: CallableParenAltContext,
            ): ExpressionTerm = build(ctx.parenExpression().expression())

            override fun visitCallableReferenceAlt(
                ctx: CallableReferenceAltContext,
            ): ExpressionTerm = ReferenceTerm.build(ctx.reference())

            override fun visitCallableTupleLiteralAlt(
                ctx: CallableTupleLiteralAltContext,
            ): ExpressionTerm = TupleLiteralTerm.build(ctx.tupleLiteral())

            override fun visitFieldReadAlt(
                ctx: FieldReadAltContext,
            ): ExpressionTerm = FieldReadTerm.build(ctx)
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

    final override fun validate(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ) {
        determineType(
            typeScope = typeScope,
            valueScope = valueScope,
        )

        validateAdditionally(
            typeScope = typeScope,
            valueScope = valueScope,
        )
    }

    open fun validateAdditionally(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ) {
    }

    abstract fun determineType(
        // Idea: Rename to metaScope?
        typeScope: StaticTypeScope,
        // Idea: Rename to staticScope?
        valueScope: StaticValueScope,
    ): Type

    // Idea for naming:
    // "environment" - scope in which an expression is evaluated
    // "context" - outer scope for nested scopes
    abstract fun evaluate(
        scope: Scope,
    ): Thunk

    abstract fun dump(): String
}
