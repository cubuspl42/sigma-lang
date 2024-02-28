package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser

data class LetInTerm(
    val block: UnorderedTupleConstructorTerm,
    val result: ExpressionTerm,
) : ExpressionTerm {
    companion object {
        fun build(
            ctx: SigmaParser.LetInContext,
        ): LetInTerm = LetInTerm(
            block = UnorderedTupleConstructorTerm.build(ctx.block),
            result = ExpressionTerm.build(ctx.result),
        )
    }
}
