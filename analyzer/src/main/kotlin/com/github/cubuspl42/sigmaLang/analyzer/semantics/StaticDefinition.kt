package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ClassDefinitionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ConstantDefinitionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntrySourceTerm

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
