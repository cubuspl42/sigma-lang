package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.TupleConstructorContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor

sealed class TupleConstructorSourceTerm : ExpressionSourceTerm() {
    companion object {
        fun build(
            ctx: TupleConstructorContext,
        ): TupleConstructorTerm = object : SigmaParserBaseVisitor<TupleConstructorTerm>() {
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
