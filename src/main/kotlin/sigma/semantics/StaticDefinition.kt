package sigma.semantics

import sigma.syntax.ClassDefinitionTerm
import sigma.syntax.ConstantDefinitionTerm
import sigma.syntax.NamespaceDefinitionTerm
import sigma.syntax.StaticStatementTerm
import sigma.syntax.TypeAliasDefinitionTerm

sealed class StaticDefinition : Declaration {
    companion object {
        fun build(
            containingNamespace: Namespace,
            term: StaticStatementTerm,
        ): StaticDefinition = when (term) {
            is ConstantDefinitionTerm -> ConstantDefinition.build(
                containingNamespace = containingNamespace,
                term = term,
            )

            is TypeAliasDefinitionTerm -> TypeAliasDefinition.build(
                containingNamespace = containingNamespace,
                term = term,
            )

            is ClassDefinitionTerm -> TODO()

            is NamespaceDefinitionTerm -> TODO()
        }
    }

    abstract val errors: Set<SemanticError>
}
