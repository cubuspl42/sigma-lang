package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.TupleConstructorContext
import sigma.parser.antlr.SigmaParserBaseVisitor

sealed class TupleConstructorSourceTerm : ExpressionSourceTerm(), TupleConstructorTerm {
    companion object {
        fun build(
            ctx: TupleConstructorContext,
        ): TupleConstructorSourceTerm = object : SigmaParserBaseVisitor<TupleConstructorSourceTerm>() {
            override fun visitOrderedTupleConstructor(
                ctx: SigmaParser.OrderedTupleConstructorContext,
            ): OrderedTupleConstructorSourceTerm {
                return OrderedTupleConstructorSourceTerm.build(ctx)
            }

            override fun visitUnorderedTupleConstructor(
                ctx: SigmaParser.UnorderedTupleConstructorContext,
            ): UnorderedTupleConstructorSourceTerm = UnorderedTupleConstructorSourceTerm.build(ctx)
        }.visit(ctx)
    }
}
