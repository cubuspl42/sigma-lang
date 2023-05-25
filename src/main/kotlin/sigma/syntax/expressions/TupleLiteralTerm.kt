package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.TupleLiteralContext
import sigma.parser.antlr.SigmaParserBaseVisitor

sealed class TupleLiteralTerm : ExpressionTerm() {
    companion object {
        fun build(
            ctx: TupleLiteralContext,
        ): TupleLiteralTerm = object : SigmaParserBaseVisitor<TupleLiteralTerm>() {
            override fun visitOrderedTupleLiteral(
                ctx: SigmaParser.OrderedTupleLiteralContext,
            ): OrderedTupleLiteralTerm {
                return OrderedTupleLiteralTerm.build(ctx)
            }

            override fun visitUnorderedTupleLiteral(
                ctx: SigmaParser.UnorderedTupleLiteralContext,
            ): UnorderedTupleLiteralTerm = UnorderedTupleLiteralTerm.build(ctx)
        }.visit(ctx)
    }
}
