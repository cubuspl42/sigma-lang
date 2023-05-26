package sigma.syntax.metaExpressions

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.TypeScope
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.ArrayTypeLiteralContext
import sigma.parser.antlr.SigmaParser.DictTypeDepictionContext
import sigma.parser.antlr.SigmaParser.FunctionTypeDepictionContext
import sigma.parser.antlr.SigmaParser.TypeExpressionContext
import sigma.parser.antlr.SigmaParserBaseVisitor
import sigma.semantics.types.Type
import sigma.syntax.Term

abstract class MetaExpressionTerm : Term() {
    companion object {
        fun build(
            ctx: TypeExpressionContext,
        ): MetaExpressionTerm = object : SigmaParserBaseVisitor<MetaExpressionTerm>() {
            override fun visitTypeCall(
                ctx: SigmaParser.TypeCallContext,
            ): MetaExpressionTerm = MetaCallTerm.build(ctx)

            override fun visitTypeReference(
                ctx: SigmaParser.TypeReferenceContext,
            ): MetaExpressionTerm = MetaReferenceTerm.build(ctx)

            override fun visitTupleTypeLiteral(
                ctx: SigmaParser.TupleTypeLiteralContext,
            ): MetaExpressionTerm = TupleTypeLiteralTerm.build(ctx)

            override fun visitFunctionTypeDepiction(
                ctx: FunctionTypeDepictionContext,
            ): MetaExpressionTerm = FunctionTypeTerm.build(ctx)

            override fun visitArrayTypeLiteral(
                ctx: ArrayTypeLiteralContext,
            ): MetaExpressionTerm = ArrayTypeLiteralTerm.build(ctx)

            override fun visitDictTypeDepiction(
                ctx: DictTypeDepictionContext,
            ): MetaExpressionTerm = DictTypeTerm.build(ctx)
        }.visit(ctx) ?: throw IllegalArgumentException("Can't match type expression ${ctx::class}")

        fun parse(
            source: String,
        ): MetaExpressionTerm {
            val sourceName = "__type_expression__"

            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)
            val parser = SigmaParser(tokenStream)

            return build(parser.typeExpression())
        }
    }

    abstract fun evaluate(
        typeScope: TypeScope,
    ): Type
}
