package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.semantics.expressions.Expression
import sigma.syntax.DefinitionTerm

class LocalDefinition(
    override val typeScope: TypeScope,
    override val term: DefinitionTerm,
    override val name: Symbol,
    override val definer: Expression,
) : Definition() {
    companion object {
        fun build(
            typeScope: TypeScope,
            declarationScope: DeclarationScope,
            term: DefinitionTerm,
        ): LocalDefinition = LocalDefinition(
            typeScope = typeScope,
            term = term,
            name = term.name,
            definer = Expression.build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term.value,
            ),
        )
    }
}
