package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.TupleConstructorContext
import sigma.parser.antlr.SigmaParserBaseVisitor

sealed class TupleConstructorTerm : ExpressionTerm() {
    companion object {
        fun build(
            ctx: TupleConstructorContext,
        ): TupleConstructorTerm = object : SigmaParserBaseVisitor<TupleConstructorTerm>() {
            override fun visitOrderedTupleConstructor(
                ctx: SigmaParser.OrderedTupleConstructorContext,
            ): OrderedTupleConstructorTerm {
                return OrderedTupleConstructorTerm.build(ctx)
            }

            override fun visitUnorderedTupleConstructor(
                ctx: SigmaParser.UnorderedTupleConstructorContext,
            ): UnorderedTupleConstructorTerm = UnorderedTupleConstructorTerm.build(ctx)
        }.visit(ctx)
    }
}
