package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationOutcome
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ConstantDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntryTerm

abstract class ConstantDefinition :
    ClassifiedDeclaration {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: NamespaceEntryTerm,
        ): UserConstantDefinition = when (term) {
            is ConstantDefinitionTerm -> UserConstantDefinition.build(
                outerScope = outerScope,
                term = term,
            )

            is NamespaceDefinitionTerm -> TODO()

            else -> throw UnsupportedOperationException("Unsupported namespace entry term: $term")
        }
    }

    fun evaluateResult(): EvaluationOutcome<Value> = valueThunk.evaluateInitial()

    abstract val valueThunk: Thunk<Value>

    final override val expressionClassification: ExpressionClassification
        get() = ConstClassification(
            constantDefinition = this,
        )
}
