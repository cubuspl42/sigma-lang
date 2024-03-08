package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value
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

    override fun transmute(): ExpressionStub<ShadowExpression> = LocalScopeStub.of(
        definitions = block.entries.mapUniquely { entry ->
            LocalScopeStub.DefinitionStub(
                key = entry.key.transmute(),
                initializerStub = entry.value.transmute(),
            )
        },
        result = result.transmute(),
    )

    override fun wrap(): Value = UnorderedTuple(
        valueByKey = mapOf(
            Identifier.of("block") to lazyOf(block.wrap()),
            Identifier.of("result") to lazyOf(result.wrap()),
        )
    )
}
