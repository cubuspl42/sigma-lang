package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.semantics.expressions.Expression
import sigma.syntax.LocalDefinitionSourceTerm

class LocalValueDefinition(
    override val declarationScope: StaticScope,
    private val term: LocalDefinitionSourceTerm,
) : ValueDefinition() {
    companion object {
        fun build(
            declarationScope: StaticScope,
            term: LocalDefinitionSourceTerm,
        ): LocalValueDefinition = LocalValueDefinition(
            declarationScope = declarationScope,
            term = term,
        )
    }

    override val name: Symbol = term.name

    override val declaredTypeBody: Expression? by lazy {
        term.declaredTypeBody?.let {
            Expression.build(
                declarationScope = declarationScope,
                term = it,
            )
        }
    }

    override val body: Expression by lazy {
        Expression.build(
            declarationScope = declarationScope,
            term = term.body,
        )
    }
}
