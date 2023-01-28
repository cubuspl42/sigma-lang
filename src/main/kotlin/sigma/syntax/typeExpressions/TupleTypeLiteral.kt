package sigma.syntax.typeExpressions

import sigma.StaticTypeScope
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.OrderedTupleTypeLiteralContext
import sigma.parser.antlr.SigmaParser.UnorderedTupleTypeLiteralContext
import sigma.parser.antlr.SigmaParserBaseVisitor
import sigma.types.TupleType
import sigma.values.tables.Scope
import sigma.values.tables.Table

abstract class TupleTypeLiteral : TypeExpression() {
    companion object {
        fun build(
            ctx: SigmaParser.TupleTypeLiteralContext,
        ): TupleTypeLiteral = object : SigmaParserBaseVisitor<TupleTypeLiteral>() {
            override fun visitUnorderedTupleTypeLiteral(
                ctx: UnorderedTupleTypeLiteralContext,
            ): TupleTypeLiteral = UnorderedTupleTypeLiteral.build(ctx)

            override fun visitOrderedTupleTypeLiteral(
                ctx: OrderedTupleTypeLiteralContext,
            ): TupleTypeLiteral = OrderedTupleTypeLiteral.build(ctx)
        }.visit(ctx)
    }

    abstract override fun evaluate(typeScope: StaticTypeScope): TupleType

    abstract fun toArgumentScope(argument: Table): Scope
}
