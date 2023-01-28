package sigma.typeExpressions

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.StaticTypeScope
import sigma.TypeReference
import sigma.expressions.Term
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.ReferenceContext
import sigma.parser.antlr.SigmaParser.TypeExpressionContext
import sigma.parser.antlr.SigmaParserBaseVisitor
import sigma.types.Type
import sigma.values.Symbol

// Thought: Move to a sub-package? And clean up subclasses locations.
abstract class TypeExpression : Term() {
    companion object {
        fun build(
            ctx: TypeExpressionContext,
        ): TypeExpression = object : SigmaParserBaseVisitor<TypeExpression>() {
            override fun visitReference(
                ctx: ReferenceContext,
            ): TypeExpression = TypeReference(
                referee = Symbol.of(ctx.referee.text),
            )

            override fun visitTupleTypeLiteral(
                ctx: SigmaParser.TupleTypeLiteralContext,
            ): TypeExpression = TupleTypeLiteral.build(ctx)

            override fun visitArrayTypeLiteral(
                ctx: SigmaParser.ArrayTypeLiteralContext,
            ): TypeExpression = ArrayTypeLiteral.build(ctx)
        }.visit(ctx) ?: throw IllegalArgumentException("Can't match type expression ${ctx::class}")

        fun parse(
            source: String,
        ): TypeExpression {
            val sourceName = "__type_expression__"

            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)
            val parser = SigmaParser(tokenStream)

            return build(parser.typeExpression())
        }
    }

    abstract fun evaluate(
        typeScope: StaticTypeScope,
    ): Type
}
