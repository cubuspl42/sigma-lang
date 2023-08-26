package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ExpressionMap
import com.github.cubuspl42.sigmaLang.analyzer.syntax.*

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

            is NamespaceDefinitionTerm -> TODO()
        }
    }

    abstract val staticValue: Thunk<Value>

    abstract val expressionMap: ExpressionMap

    abstract val errors: Set<SemanticError>
}
