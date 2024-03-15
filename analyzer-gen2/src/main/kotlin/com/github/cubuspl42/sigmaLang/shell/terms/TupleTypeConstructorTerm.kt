package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import com.github.cubuspl42.sigmaLang.core.values.Identifier

sealed class TupleTypeConstructorTerm : Term {
    companion object : Term.Builder<SigmaParser.TupleTypeConstructorContext, TupleTypeConstructorTerm>() {
        override fun build(
            ctx: SigmaParser.TupleTypeConstructorContext,
        ): TupleTypeConstructorTerm = object : SigmaParserBaseVisitor<TupleTypeConstructorTerm>() {
            override fun visitOrderedTupleTypeConstructor(
                ctx: SigmaParser.OrderedTupleTypeConstructorContext,
            ) = OrderedTupleTypeConstructorTerm.build(ctx)

            override fun visitUnorderedTupleTypeConstructor(
                ctx: SigmaParser.UnorderedTupleTypeConstructorContext,
            ) = UnorderedTupleTypeConstructorTerm.build(ctx)
        }.visit(ctx)

        override fun extract(parser: SigmaParser): SigmaParser.TupleTypeConstructorContext =
            parser.tupleTypeConstructor()
    }

    abstract val names: Iterable<Identifier>
}
