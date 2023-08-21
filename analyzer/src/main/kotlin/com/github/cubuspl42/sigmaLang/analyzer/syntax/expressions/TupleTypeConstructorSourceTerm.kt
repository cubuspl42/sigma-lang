package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.OrderedTupleTypeConstructorContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.UnorderedTupleTypeConstructorContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor

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
