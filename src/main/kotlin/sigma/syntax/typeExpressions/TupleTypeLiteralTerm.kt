package sigma.syntax.typeExpressions

import sigma.TypeScope
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParserBaseVisitor
import sigma.semantics.types.TupleType
import sigma.syntax.SourceLocation

data class TupleTypeLiteralTerm(
    override val location: SourceLocation,
    val body: TupleTypeLiteralBodyTerm,
) : TypeExpressionTerm() {
    companion object {
        fun build(
            ctx: SigmaParser.TupleTypeLiteralBodyContext,
        ): TupleTypeLiteralBodyTerm = object : SigmaParserBaseVisitor<TupleTypeLiteralBodyTerm>() {
            override fun visitUnorderedTupleTypeLiteralBody(
                ctx: SigmaParser.UnorderedTupleTypeLiteralBodyContext,
            ): TupleTypeLiteralBodyTerm = UnorderedTupleTypeLiteralBodyTerm.build(ctx)

            override fun visitOrderedTupleTypeLiteralBody(
                ctx: SigmaParser.OrderedTupleTypeLiteralBodyContext,
            ): TupleTypeLiteralBodyTerm = OrderedTupleTypeLiteralBodyTerm.build(ctx)
        }.visit(ctx)
    }

    override fun evaluate(
        typeScope: TypeScope,
    ): TupleType = body.evaluate(typeScope = typeScope)
}
