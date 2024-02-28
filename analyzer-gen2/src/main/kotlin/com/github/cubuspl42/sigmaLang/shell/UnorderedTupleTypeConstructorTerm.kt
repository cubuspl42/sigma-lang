package com.github.cubuspl42.sigmaLang.shell

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser

data class UnorderedTupleTypeConstructorTerm(
    val body: UnorderedTupleConstructorTerm,
) : ExpressionTerm {
    companion object {
        fun build(
            ctx: SigmaParser.UnorderedTupleTypeConstructorContext,
        ): UnorderedTupleTypeConstructorTerm = UnorderedTupleTypeConstructorTerm(
            body = UnorderedTupleConstructorTerm.build(ctx.body),
        )
    }
}
