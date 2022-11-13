package sigma.expressions

import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.TupleContext
import sigma.parser.antlr.SigmaParserBaseVisitor

abstract class TupleLiteral : Expression() {
    companion object {
        fun build(ctx: TupleContext): TupleLiteral {
            return object : SigmaParserBaseVisitor<TupleLiteral>() {
                override fun visitOrderedTupleAlt(
                    ctx: SigmaParser.OrderedTupleAltContext,
                ): OrderedTupleLiteral = OrderedTupleLiteral.build(ctx.content)

                override fun visitUnorderedTupleAlt(
                    ctx: SigmaParser.UnorderedTupleAltContext,
                ): TableConstructor = TableConstructor.build(ctx.content)
            }.visit(ctx)
        }
    }
}
