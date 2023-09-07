package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.OrderedTupleTypeConstructorContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.UnorderedTupleTypeConstructorContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Constness
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

sealed class TupleTypeConstructorSourceTerm : ExpressionSourceTerm(), TupleTypeConstructorTerm {
    companion object {
        fun build(
            ctx: SigmaParser.TupleTypeConstructorContext,
        ): TupleTypeConstructorSourceTerm {
            val location = SourceLocation.build(ctx)
            val constness = Constness.build(ctx.constnessMarker)

            return object : SigmaParserBaseVisitor<TupleTypeConstructorSourceTerm>() {
                override fun visitUnorderedTupleTypeConstructor(
                    ctx: UnorderedTupleTypeConstructorContext,
                ): TupleTypeConstructorSourceTerm = UnorderedTupleTypeConstructorSourceTerm.build(
                    location = location,
                    constness = constness,
                    ctx = ctx,
                )

                override fun visitOrderedTupleTypeConstructor(
                    ctx: OrderedTupleTypeConstructorContext,
                ): TupleTypeConstructorSourceTerm = OrderedTupleTypeConstructorSourceTerm.build(
                    location = location,
                    constness = constness,
                    ctx = ctx,
                )
            }.visit(ctx.tupleTypeConstructorBody())
        }
    }

//    abstract override fun evaluate(declarationScope: StaticScope): TupleType
}
