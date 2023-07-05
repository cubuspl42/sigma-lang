package sigma.semantics

import sigma.syntax.ConstantDefinitionTerm
import sigma.syntax.StaticStatementTerm
import sigma.syntax.TypeAliasDefinitionTerm

sealed class StaticStatement : Entity() {
    companion object {
        fun build(
            containingModule: Module,
            term: StaticStatementTerm,
        ): StaticStatement = when(term) {
            is ConstantDefinitionTerm -> ConstantDefinition.build(
                containingModule = containingModule,
                term = term,
            )

            is TypeAliasDefinitionTerm -> TODO()
        }
    }
}
