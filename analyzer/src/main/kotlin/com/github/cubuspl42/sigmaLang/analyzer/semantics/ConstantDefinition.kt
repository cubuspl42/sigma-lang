package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationOutcome
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ExpressionMap
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ConstantDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntryTerm

abstract class ConstantDefinition {
    companion object {
        fun build(
            containingNamespaceDefinition: NamespaceDefinition,
            term: NamespaceEntryTerm,
        ): ConstantDefinition = when (term) {
            is ConstantDefinitionTerm -> UserConstantDefinition.build(
                containingNamespaceDefinition = containingNamespaceDefinition,
                term = term,
            )

            is NamespaceDefinitionTerm -> TODO()

            else -> throw UnsupportedOperationException("Unsupported namespace entry term: $term")
        }
    }

    fun evaluateResult(): EvaluationOutcome<Value> = valueThunk.evaluateInitial()

    abstract val name: Symbol

    abstract val valueThunk: Thunk<Value>

    abstract val effectiveType: Thunk<Type>

    abstract val expressionMap: ExpressionMap

    abstract val errors: Set<SemanticError>
}
