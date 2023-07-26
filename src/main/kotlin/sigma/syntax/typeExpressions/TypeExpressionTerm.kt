package sigma.syntax.typeExpressions

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.ArrayTypeConstructorContext
import sigma.parser.antlr.SigmaParser.DictTypeDepictionContext
import sigma.parser.antlr.SigmaParser.FunctionTypeDepictionContext
import sigma.parser.antlr.SigmaParser.TypeExpressionContext
import sigma.parser.antlr.SigmaParserBaseVisitor
import sigma.semantics.StaticScope
import sigma.semantics.types.Type
import sigma.semantics.types.TypeEntity
import sigma.syntax.Term

abstract class TypeExpressionTerm : Term() {
    companion object {
        fun build(
            ctx: TypeExpressionContext,
        ): TypeExpressionTerm = object : SigmaParserBaseVisitor<TypeExpressionTerm>() {
            override fun visitTypeCall(
                ctx: SigmaParser.TypeCallContext,
            ): TypeExpressionTerm = TypeCallTerm.build(ctx)

            override fun visitTypeReference(
                ctx: SigmaParser.TypeReferenceContext,
            ): TypeExpressionTerm = TypeReferenceTerm.build(ctx)

            override fun visitTupleTypeConstructor(
                ctx: SigmaParser.TupleTypeConstructorContext,
            ): TypeExpressionTerm = TupleTypeConstructorTerm.build(ctx)

            override fun visitFunctionTypeDepiction(
                ctx: FunctionTypeDepictionContext,
            ): TypeExpressionTerm = FunctionTypeTerm.build(ctx)

            override fun visitArrayTypeConstructor(
                ctx: ArrayTypeConstructorContext,
            ): TypeExpressionTerm = ArrayTypeConstructorTerm.build(ctx)

            override fun visitDictTypeDepiction(
                ctx: DictTypeDepictionContext,
            ): TypeExpressionTerm = DictTypeTerm.build(ctx)

            override fun visitGenericTypeConstructor(
                ctx: SigmaParser.GenericTypeConstructorContext,
            ): TypeExpressionTerm {
                return GenericTypeConstructorTerm.build(ctx)
            }
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
        declarationScope: StaticScope,
    ): TypeEntity

    fun evaluateAsType(
        declarationScope: StaticScope,
    ): Type =
        // TODO: Improve the error handling
        evaluate(declarationScope = declarationScope) as Type
}
