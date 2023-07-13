package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.semantics.types.Type
import sigma.syntax.ClassDefinitionTerm
import sigma.syntax.ConstantDefinitionTerm
import sigma.syntax.NamespaceDefinitionTerm
import sigma.syntax.StaticStatementTerm
import sigma.syntax.TypeAliasDefinitionTerm

sealed class StaticDefinition {
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

            is ClassDefinitionTerm -> ClassDefinition.build()

            is NamespaceDefinitionTerm -> TODO()
        }
    }

    abstract val errors: Set<SemanticError>

    abstract val name: Symbol

    open val definedValue: Value? = null

    open val definedType: Type? = null
}
