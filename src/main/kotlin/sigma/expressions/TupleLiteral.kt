package sigma.expressions

import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.TupleLiteralContext
import sigma.parser.antlr.SigmaParserBaseVisitor

abstract class TupleLiteral : Expression() {
    companion object {
        fun build(
            ctx: TupleLiteralContext,
        ): TupleLiteral = object : SigmaParserBaseVisitor<TupleLiteral>() {
            override fun visitOrderedTupleLiteralAlt(
                ctx: SigmaParser.OrderedTupleLiteralAltContext,
            ): OrderedTupleLiteral {
                return OrderedTupleLiteral.build(ctx.content)
            }

            override fun visitUnorderedTupleLiteralAlt(
                ctx: SigmaParser.UnorderedTupleLiteralAltContext,
            ): TableConstructor = TableConstructor.build(ctx.content)
        }.visit(ctx)
    }
}
