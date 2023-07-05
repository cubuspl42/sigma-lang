package sigma.semantics

import sigma.syntax.ConstantDefinitionTerm
import sigma.syntax.StaticStatementTerm

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
        }
    }
}
