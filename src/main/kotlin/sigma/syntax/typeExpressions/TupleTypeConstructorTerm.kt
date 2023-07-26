package sigma.syntax.typeExpressions

import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.OrderedTupleTypeConstructorContext
import sigma.parser.antlr.SigmaParser.UnorderedTupleTypeConstructorContext
import sigma.parser.antlr.SigmaParserBaseVisitor
import sigma.semantics.StaticScope
import sigma.semantics.types.TupleType

abstract class TupleTypeConstructorTerm : TypeExpressionTerm() {
    companion object {
        fun build(
            ctx: SigmaParser.TupleTypeConstructorContext,
        ): TupleTypeConstructorTerm = object : SigmaParserBaseVisitor<TupleTypeConstructorTerm>() {
            override fun visitUnorderedTupleTypeConstructor(
                ctx: UnorderedTupleTypeConstructorContext,
            ): TupleTypeConstructorTerm = UnorderedTupleTypeConstructorTerm.build(ctx)

            override fun visitOrderedTupleTypeConstructor(
                ctx: OrderedTupleTypeConstructorContext,
            ): TupleTypeConstructorTerm = OrderedTupleTypeConstructorTerm.build(ctx)
        }.visit(ctx)
    }

    abstract override fun evaluate(declarationScope: StaticScope): TupleType
}
