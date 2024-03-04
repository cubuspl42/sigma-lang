package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.shell.ConstructionContext
import com.github.cubuspl42.sigmaLang.shell.scope.DefinitionScope
import com.github.cubuspl42.sigmaLang.shell.scope.chainWith

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

    override fun construct(context: ConstructionContext): Lazy<Expression> {
        val innerScope = DefinitionScope.construct(
            context = context,
            definitionBlock = block,
        ).chainWith(context.scope)

        return result.construct(
            context = context.copy(
                scope = innerScope,
            ),
        )
    }
}
