package sigma.syntax.typeExpressions

import sigma.StaticTypeScope
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.OrderedTupleTypeLiteralContext
import sigma.parser.antlr.SigmaParser.UnorderedTupleTypeLiteralContext
import sigma.parser.antlr.SigmaParserBaseVisitor
import sigma.semantics.types.TupleType
import sigma.values.tables.Scope
import sigma.values.tables.Table

abstract class TupleTypeLiteralTerm : TypeExpressionTerm() {
    companion object {
        fun build(
            ctx: SigmaParser.TupleTypeLiteralContext,
        ): TupleTypeLiteralTerm = object : SigmaParserBaseVisitor<TupleTypeLiteralTerm>() {
            override fun visitUnorderedTupleTypeLiteral(
                ctx: UnorderedTupleTypeLiteralContext,
            ): TupleTypeLiteralTerm = UnorderedTupleTypeLiteralTerm.build(ctx)

            override fun visitOrderedTupleTypeLiteral(
                ctx: OrderedTupleTypeLiteralContext,
            ): TupleTypeLiteralTerm = OrderedTupleTypeLiteralTerm.build(ctx)
        }.visit(ctx)
    }

    abstract override fun evaluate(typeScope: StaticTypeScope): TupleType

    abstract fun toArgumentScope(argument: Table): Scope
}
