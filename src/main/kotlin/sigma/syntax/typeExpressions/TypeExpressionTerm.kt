package sigma.syntax.typeExpressions

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.SyntaxTypeScope
import sigma.TypeReferenceTerm
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.ArrayTypeLiteralContext
import sigma.parser.antlr.SigmaParser.DictTypeDepictionContext
import sigma.parser.antlr.SigmaParser.FunctionTypeDepictionContext
import sigma.parser.antlr.SigmaParser.ReferenceContext
import sigma.parser.antlr.SigmaParser.TypeExpressionContext
import sigma.parser.antlr.SigmaParserBaseVisitor
import sigma.syntax.SourceLocation
import sigma.syntax.Term
import sigma.semantics.types.Type
import sigma.values.Symbol

abstract class TypeExpressionTerm : Term() {
    companion object {
        fun build(
            ctx: TypeExpressionContext,
        ): TypeExpressionTerm = object : SigmaParserBaseVisitor<TypeExpressionTerm>() {
            override fun visitTupleTypeLiteral(
                ctx: SigmaParser.TupleTypeLiteralContext,
            ): TypeExpressionTerm = TupleTypeLiteralTerm.build(ctx)

            override fun visitFunctionTypeDepiction(
                ctx: FunctionTypeDepictionContext,
            ): TypeExpressionTerm = FunctionTypeTerm.build(ctx)

            override fun visitArrayTypeLiteral(
                ctx: ArrayTypeLiteralContext,
            ): TypeExpressionTerm = ArrayTypeLiteralTerm.build(ctx)

            override fun visitDictTypeDepiction(
                ctx: DictTypeDepictionContext,
            ): TypeExpressionTerm = DictTypeTerm.build(ctx)

            override fun visitReference(
                ctx: ReferenceContext,
            ): TypeExpressionTerm = TypeReferenceTerm(
                location = SourceLocation.build(ctx),
                referee = Symbol.of(ctx.referee.text),
            )
        }.visit(ctx) ?: throw IllegalArgumentException("Can't match type expression ${ctx::class}")

        fun parse(
            source: String,
        ): TypeExpressionTerm {
            val sourceName = "__type_expression__"

            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)
            val parser = SigmaParser(tokenStream)

            return build(parser.typeExpression())
        }
    }

    abstract fun evaluate(
        typeScope: SyntaxTypeScope,
    ): Type
}
