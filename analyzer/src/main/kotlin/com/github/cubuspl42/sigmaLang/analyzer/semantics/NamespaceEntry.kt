package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ExpressionMap
import com.github.cubuspl42.sigmaLang.analyzer.syntax.*

abstract class NamespaceEntry : Declaration {
    companion object {
        fun build(
            containingNamespace: Namespace,
            term: NamespaceEntryTerm,
        ): NamespaceEntry = when (term) {
            is ConstantDefinitionTerm -> ConstantDefinition.build(
                containingNamespace = containingNamespace,
                term = term,
            )

            is NamespaceDefinitionTerm -> TODO()

            else -> throw UnsupportedOperationException("Unsupported namespace entry term: $term")
        }
    }

    abstract val staticValue: Thunk<Value>

    abstract val expressionMap: ExpressionMap

    abstract val errors: Set<SemanticError>
}
