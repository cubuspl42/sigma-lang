package sigma.syntax.typeExpressions

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.StaticTypeScope
import sigma.TypeReference
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.FunctionTypeDepictionContext
import sigma.parser.antlr.SigmaParser.ReferenceContext
import sigma.parser.antlr.SigmaParser.TypeExpressionContext
import sigma.parser.antlr.SigmaParserBaseVisitor
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.Term
import sigma.types.Type
import sigma.values.Symbol

abstract class TypeExpression : Term() {
    companion object {
        fun build(
            ctx: TypeExpressionContext,
        ): TypeExpression = object : SigmaParserBaseVisitor<TypeExpression>() {
            override fun visitTupleTypeLiteral(
                ctx: SigmaParser.TupleTypeLiteralContext,
            ): TypeExpression = TupleTypeLiteral.build(ctx)

            override fun visitFunctionTypeDepiction(
                ctx: FunctionTypeDepictionContext,
            ): TypeExpression = FunctionTypeDepiction.build(ctx)

            override fun visitArrayTypeLiteral(
                ctx: SigmaParser.ArrayTypeLiteralContext,
            ): TypeExpression = ArrayTypeLiteral.build(ctx)

            override fun visitReference(
                ctx: ReferenceContext,
            ): TypeExpression = TypeReference(
                location = SourceLocation.build(ctx),
                referee = Symbol.of(ctx.referee.text),
            )
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
