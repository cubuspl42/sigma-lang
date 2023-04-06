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
import sigma.parser.antlr.SigmaParser.CallableTupleLiteralAltContext
import sigma.parser.antlr.SigmaParser.CallableExpressionAltContext
import sigma.parser.antlr.SigmaParser.CallableParenAltContext
import sigma.parser.antlr.SigmaParser.CallableReferenceAltContext
import sigma.parser.antlr.SigmaParser.DictLiteralAltContext
import sigma.parser.antlr.SigmaParser.TupleLiteralAltContext
import sigma.parser.antlr.SigmaParser.IntLiteralAltContext
import sigma.parser.antlr.SigmaParser.IsUndefinedCheckAltContext
import sigma.parser.antlr.SigmaParser.LetExpressionAltContext
import sigma.parser.antlr.SigmaParser.ParenExpressionAltContext
import sigma.parser.antlr.SigmaParser.ReferenceAltContext
import sigma.parser.antlr.SigmaParser.SymbolLiteralAltContext
import sigma.parser.antlr.SigmaParserBaseVisitor
import sigma.semantics.types.Type
import sigma.syntax.Term
import sigma.values.Value
import sigma.values.tables.Scope

sealed class Expression : Term() {
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
            ctx: ParserRuleContext,
        ): Expression = object : SigmaParserBaseVisitor<Expression>() {
            override fun visitBinaryOperationAlt(
                ctx: BinaryOperationAltContext,
            ): Expression = Call.build(ctx)

            override fun visitParenExpressionAlt(
                ctx: ParenExpressionAltContext,
            ): Expression = build(ctx.parenExpression().expression())

            override fun visitReferenceAlt(
                ctx: ReferenceAltContext,
            ): Expression = Reference.build(ctx.reference())

            override fun visitAbstractionAlt(
                ctx: AbstractionAltContext,
            ): Expression = Abstraction.build(ctx.abstraction())

            override fun visitTupleLiteralAlt(
                ctx: TupleLiteralAltContext,
            ): Expression = TupleLiteral.build(ctx.tupleLiteral())

            override fun visitDictLiteralAlt(
                ctx: DictLiteralAltContext,
            ): Expression = DictLiteral.build(ctx.dictLiteral())

            override fun visitLetExpressionAlt(
                ctx: LetExpressionAltContext,
            ): Expression = LetExpression.build(ctx.letExpression())

            override fun visitIsUndefinedCheckAlt(
                ctx: IsUndefinedCheckAltContext,
            ): Expression = IsUndefinedCheck.build(ctx.isUndefinedCheck)

            override fun visitSymbolLiteralAlt(
                ctx: SymbolLiteralAltContext,
            ): Expression = SymbolLiteral.build(ctx)

            override fun visitIntLiteralAlt(
                ctx: IntLiteralAltContext,
            ): Expression = IntLiteral.build(ctx)

            override fun visitCallableExpressionAlt(
                ctx: CallableExpressionAltContext,
            ): Expression = build(ctx.callableExpression())

            override fun visitCallExpressionAlt(
                ctx: CallExpressionAltContext,
            ): Expression = Call.build(ctx)

            override fun visitCallExpressionTupleLiteralAlt(
                ctx: CallExpressionTupleLiteralAltContext,
            ): Expression = Call.build(ctx)

            override fun visitCallableParenAlt(
                ctx: CallableParenAltContext,
            ): Expression = build(ctx.parenExpression().expression())

            override fun visitCallableReferenceAlt(
                ctx: CallableReferenceAltContext,
            ): Expression = Reference.build(ctx.reference())

            override fun visitCallableTupleLiteralAlt(
                ctx: CallableTupleLiteralAltContext,
            ): Expression = TupleLiteral.build(ctx.tupleLiteral())
        }.visit(ctx) ?: throw IllegalArgumentException("Can't match expression ${ctx::class}")
    }

    inner class BoundThunk(private val scope: Scope) : Thunk() {
        override val toEvaluatedValue: Value by lazy {
            this@Expression.evaluate(
                scope = scope,
            ).toEvaluatedValue
        }

        override fun dump(): String = "(bound thunk)"
    }

    // Isn't this the same as `evaluate`?
    fun bind(scope: Scope): Thunk = BoundThunk(scope = scope)

    fun evaluateAsRoot(): Value = evaluate(scope = BuiltinScope).toEvaluatedValue

    fun validateAndInferType(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ): Type {
        validate(typeScope = typeScope, valueScope = valueScope)

        return inferType(typeScope = typeScope, valueScope = valueScope)
    }

    // Thought: Rename to `determineType`? Or not?
    abstract fun inferType(
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
