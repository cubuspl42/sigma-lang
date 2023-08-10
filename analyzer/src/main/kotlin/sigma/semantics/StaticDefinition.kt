package sigma.semantics

import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.syntax.ClassDefinitionTerm
import sigma.syntax.ConstantDefinitionTerm
import sigma.syntax.NamespaceDefinitionTerm
import sigma.syntax.NamespaceEntryTerm

abstract class StaticDefinition : Declaration {
    companion object {
        fun build(
            containingNamespace: Namespace,
            term: NamespaceEntryTerm,
        ): StaticDefinition = when (term) {
            is ConstantDefinitionTerm -> ConstantDefinition.build(
                containingNamespace = containingNamespace,
                term = term,
            )

            is ClassDefinitionTerm -> TODO()

            is NamespaceDefinitionTerm -> TODO()
        }
    }

    abstract val staticValue: Thunk<Value>

    abstract val errors: Set<SemanticError>
}
