package sigma.expressions

import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.TupleLiteralContext
import sigma.parser.antlr.SigmaParserBaseVisitor

abstract class TupleLiteral : Expression() {
    companion object {
        fun build(
            ctx: TupleLiteralContext,
        ): TupleLiteral = object : SigmaParserBaseVisitor<TupleLiteral>() {
            override fun visitOrderedTupleLiteral(
                ctx: SigmaParser.OrderedTupleLiteralContext,
            ): OrderedTupleLiteral {
                return OrderedTupleLiteral.build(ctx)
            }

            override fun visitUnorderedTupleLiteral(
                ctx: SigmaParser.UnorderedTupleLiteralContext,
            ): UnorderedTupleLiteral = UnorderedTupleLiteral.build(ctx)
        }.visit(ctx)
    }
}
