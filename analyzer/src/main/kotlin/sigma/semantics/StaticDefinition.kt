package sigma.semantics

import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.syntax.ClassDefinitionSourceTerm
import sigma.syntax.ConstantDefinitionSourceTerm
import sigma.syntax.NamespaceDefinitionSourceTerm
import sigma.syntax.NamespaceEntrySourceTerm

abstract class StaticDefinition : Declaration {
    companion object {
        fun build(
            containingNamespace: Namespace,
            term: NamespaceEntrySourceTerm,
        ): StaticDefinition = when (term) {
            is ConstantDefinitionSourceTerm -> ConstantDefinition.build(
                containingNamespace = containingNamespace,
                term = term,
            )

            is ClassDefinitionSourceTerm -> TODO()

            is NamespaceDefinitionSourceTerm -> TODO()
        }
    }

    abstract val staticValue: Thunk<Value>

    abstract val errors: Set<SemanticError>
}
