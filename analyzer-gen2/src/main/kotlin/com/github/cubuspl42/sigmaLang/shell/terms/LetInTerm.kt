package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.KnotConstructor
import com.github.cubuspl42.sigmaLang.shell.ConstructionContext
import com.github.cubuspl42.sigmaLang.shell.scope.KnotScope
import com.github.cubuspl42.sigmaLang.shell.scope.StaticScope
import com.github.cubuspl42.sigmaLang.shell.scope.chainWith
import com.github.cubuspl42.sigmaLang.utils.LazyUtils

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

    override fun construct(context: ConstructionContext): Lazy<KnotConstructor> {
        val knotConstructor = LazyUtils.looped { knotConstructorLooped ->
            val innerContext = context.copy(
                scope = KnotScope(
                    knotConstructorLazy = knotConstructorLooped,
                ).chainWith(context.scope),
            )

            return@looped KnotConstructor(
                definitionByIdentifier = block.entries.associate {
                    it.key.construct() to it.value.construct(
                        context = innerContext,
                    )
                },
                resultLazy = result.construct(
                    context = innerContext,
                ),
            )
        }

        return lazyOf(knotConstructor)
    }
}
