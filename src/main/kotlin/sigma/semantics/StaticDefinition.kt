package sigma.semantics

import sigma.evaluation.Thunk
import sigma.evaluation.values.Symbol
import sigma.semantics.types.Type
import sigma.syntax.ConstantDefinitionTerm
import sigma.syntax.StaticStatementTerm
import sigma.syntax.TypeAliasDefinitionTerm

sealed class StaticDefinition : Entity() {
    companion object {
        fun build(
            containingModule: Module,
            term: StaticStatementTerm,
        ): StaticDefinition = when(term) {
            is ConstantDefinitionTerm -> ConstantDefinition.build(
                containingModule = containingModule,
                term = term,
            )

            is TypeAliasDefinitionTerm -> TypeAliasDefinition.build(
                containingModule = containingModule,
                term = term,
            )
        }
    }

    abstract val name: Symbol

    open val definedValue: Thunk? = null

    open val definedType: Type? = null
}
