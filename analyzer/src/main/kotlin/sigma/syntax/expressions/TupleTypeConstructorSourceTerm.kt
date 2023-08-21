package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.OrderedTupleTypeConstructorContext
import sigma.parser.antlr.SigmaParser.UnorderedTupleTypeConstructorContext
import sigma.parser.antlr.SigmaParserBaseVisitor

sealed class TupleTypeConstructorSourceTerm : ExpressionSourceTerm(), TupleTypeConstructorTerm {
    companion object {
        fun build(
            ctx: SigmaParser.TupleTypeConstructorContext,
        ): TupleTypeConstructorSourceTerm = object : SigmaParserBaseVisitor<TupleTypeConstructorSourceTerm>() {
            override fun visitUnorderedTupleTypeConstructor(
                ctx: UnorderedTupleTypeConstructorContext,
            ): TupleTypeConstructorSourceTerm = UnorderedTupleTypeConstructorSourceTerm.build(ctx)

            override fun visitOrderedTupleTypeConstructor(
                ctx: OrderedTupleTypeConstructorContext,
            ): TupleTypeConstructorSourceTerm = OrderedTupleTypeConstructorSourceTerm.build(ctx)
        }.visit(ctx)
    }

//    abstract override fun evaluate(declarationScope: StaticScope): TupleType
}
