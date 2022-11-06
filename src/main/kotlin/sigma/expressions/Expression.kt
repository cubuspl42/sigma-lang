package sigma.expressions

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
import sigma.parser.antlr.SigmaParser.CallExpressionDictAltContext
import sigma.parser.antlr.SigmaParser.CallableDictAltContext
import sigma.parser.antlr.SigmaParser.CallableExpressionAltContext
import sigma.parser.antlr.SigmaParser.CallableParenAltContext
import sigma.parser.antlr.SigmaParser.CallableReferenceAltContext
import sigma.parser.antlr.SigmaParser.DictAltContext
import sigma.parser.antlr.SigmaParser.IntLiteralAltContext
import sigma.parser.antlr.SigmaParser.LetExpressionAltContext
import sigma.parser.antlr.SigmaParser.ParenExpressionAltContext
import sigma.parser.antlr.SigmaParser.ReferenceAltContext
import sigma.parser.antlr.SigmaParser.SymbolLiteralAltContext
import sigma.parser.antlr.SigmaParserBaseVisitor
import sigma.types.Type
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
            expression: ParserRuleContext,
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

            override fun visitDictAlt(
                ctx: DictAltContext,
            ): Expression = TableConstructor.build(ctx.dict())

            override fun visitLetExpressionAlt(
                ctx: LetExpressionAltContext,
            ): Expression = LetExpression.build(ctx.letExpression())

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

            override fun visitCallExpressionDictAlt(
                ctx: CallExpressionDictAltContext,
            ): Expression = Call.build(ctx)

            override fun visitCallableParenAlt(
                ctx: CallableParenAltContext,
            ): Expression = build(ctx.parenExpression().expression())

            override fun visitCallableReferenceAlt(
                ctx: CallableReferenceAltContext,
            ): Expression = Reference.build(ctx.reference())

            override fun visitCallableDictAlt(
                ctx: CallableDictAltContext,
            ): Expression = TableConstructor.build(ctx.dict())
        }.visit(expression) ?: throw IllegalArgumentException("Can't match expression ${expression::class}")
    }

    inner class BoundThunk(private val scope: Scope) : Thunk() {
        override val toEvaluatedValue: Value by lazy {
            this@Expression.evaluate(
                scope = scope,
            ).toEvaluatedValue
        }

        override fun dump(): String = "(bound thunk)"
    }

    fun bind(scope: Scope): Thunk = BoundThunk(scope = scope)

    fun evaluateAsRoot(): Value = evaluate(scope = BuiltinScope).toEvaluatedValue

    fun validateAndInferType(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ): Type {
        validate(typeScope = typeScope, valueScope = valueScope)

        return inferType(typeScope = typeScope, valueScope = valueScope)
    }

    abstract fun inferType(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ): Type

    open fun validate(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ) {
    }

    abstract fun evaluate(
        scope: Scope,
    ): Thunk

    abstract fun dump(): String
}
