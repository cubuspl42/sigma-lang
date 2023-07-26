package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.semantics.expressions.Expression
import sigma.syntax.LocalDefinitionTerm

class LocalValueDefinition(
    override val declarationScope: StaticScope,
    override val term: LocalDefinitionTerm,
) : ValueDefinition() {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: LocalDefinitionTerm,
        ): LocalValueDefinition = LocalValueDefinition(
            declarationScope = declarationScope,
            term = term,
        )
    }

    override val name: Symbol = term.name

    override val definer: Expression by lazy {
        Expression.build(
            declarationScope = declarationScope,
            term = term.definer,
        )
    }
}
