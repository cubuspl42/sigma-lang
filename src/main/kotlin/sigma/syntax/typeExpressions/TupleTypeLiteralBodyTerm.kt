package sigma.syntax.typeExpressions

import sigma.TypeScope
import sigma.evaluation.scope.Scope
import sigma.evaluation.values.tables.Table
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.OrderedTupleTypeLiteralBodyContext
import sigma.parser.antlr.SigmaParser.UnorderedTupleTypeLiteralBodyContext
import sigma.parser.antlr.SigmaParserBaseVisitor
import sigma.semantics.types.TupleType
import sigma.syntax.Term

abstract class TupleTypeLiteralBodyTerm : Term() {
    companion object {
        fun build(
            ctx: SigmaParser.TupleTypeLiteralBodyContext,
        ): TupleTypeLiteralBodyTerm = object : SigmaParserBaseVisitor<TupleTypeLiteralBodyTerm>() {
            override fun visitUnorderedTupleTypeLiteralBody(
                ctx: UnorderedTupleTypeLiteralBodyContext,
            ): TupleTypeLiteralBodyTerm = UnorderedTupleTypeLiteralBodyTerm.build(ctx)

            override fun visitOrderedTupleTypeLiteralBody(
                ctx: OrderedTupleTypeLiteralBodyContext,
            ): TupleTypeLiteralBodyTerm = OrderedTupleTypeLiteralBodyTerm.build(ctx)
        }.visit(ctx)
    }

    abstract fun evaluate(typeScope: TypeScope): TupleType

    abstract fun toArgumentScope(argument: Table): Scope
}
