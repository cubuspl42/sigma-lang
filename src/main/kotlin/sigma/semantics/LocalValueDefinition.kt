package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.semantics.expressions.Expression
import sigma.syntax.LocalDefinitionTerm

class LocalValueDefinition(
    override val typeScope: TypeScope,
    override val term: LocalDefinitionTerm,
    override val name: Symbol,
    override val definer: Expression,
) : ValueDefinition() {
    companion object {
        fun build(
            typeScope: TypeScope,
            declarationScope: DeclarationScope,
            term: LocalDefinitionTerm,
        ): LocalValueDefinition = LocalValueDefinition(
            typeScope = typeScope,
            term = term,
            name = term.name,
            definer = Expression.build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term.definer,
            ),
        )
    }
}