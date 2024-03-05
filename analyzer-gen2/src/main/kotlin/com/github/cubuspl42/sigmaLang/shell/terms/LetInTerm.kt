package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub
import com.github.cubuspl42.sigmaLang.shell.stubs.LocalScopeStub
import com.github.cubuspl42.sigmaLang.utils.mapUniquely

data class LetInTerm(
    val block: UnorderedTupleConstructorTerm,
    val result: ExpressionTerm,
) : ExpressionTerm {
    companion object : Term.Builder<SigmaParser.LetInContext, LetInTerm>() {
        override fun build(
            ctx: SigmaParser.LetInContext,
        ): LetInTerm = LetInTerm(
            block = UnorderedTupleConstructorTerm.build(ctx.unorderedTupleConstructor()),
            result = ExpressionTerm.build(ctx.expression()),
        )

        override fun extract(parser: SigmaParser): SigmaParser.LetInContext = parser.letIn()
    }

    override fun transmute(): ExpressionStub<*> = LocalScopeStub(
        definitions = block.entries.mapUniquely { entry ->
            LocalScopeStub.DefinitionStub(
                key = entry.key.transmute(),
                initializer = entry.value.transmute(),
            )
        },
        result = result.transmute(),
    )
}
