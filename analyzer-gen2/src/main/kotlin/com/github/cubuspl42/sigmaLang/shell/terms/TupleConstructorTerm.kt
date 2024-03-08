package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor

abstract class TupleConstructorTerm : ExpressionTerm {
    companion object : Term.Builder<SigmaParser.TupleConstructorContext, TupleConstructorTerm>() {
        val Empty: UnorderedTupleConstructorTerm = UnorderedTupleConstructorTerm(
            entries = emptyList(),
        )

        override fun build(
            ctx: SigmaParser.TupleConstructorContext,
        ): TupleConstructorTerm = object : SigmaParserBaseVisitor<TupleConstructorTerm>() {
            override fun visitUnorderedTupleConstructor(
                ctx: SigmaParser.UnorderedTupleConstructorContext,
            ): TupleConstructorTerm = UnorderedTupleConstructorTerm.build(ctx)

            override fun visitOrderedTupleConstructor(
                ctx: SigmaParser.OrderedTupleConstructorContext,
            ): TupleConstructorTerm = OrderedTupleConstructorTerm.build(ctx)
        }.visit(ctx)

        override fun extract(parser: SigmaParser): SigmaParser.TupleConstructorContext = parser.tupleConstructor()
    }
}
