package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationOutcome
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstClassification
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ExpressionClassification
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ExpressionMap
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ClassDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ConstantDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntryTerm

abstract class ConstantDefinition : ClassifiedIntroduction, Definition {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: NamespaceEntryTerm,
        ): ConstantDefinition = when (term) {
            is ConstantDefinitionTerm -> UserConstantDefinition.build(
                outerScope = outerScope,
                term = term,
            )

            is ClassDefinitionTerm -> ClassDefinition.build(
                outerScope = outerScope,
                term = term,
            )

            is NamespaceDefinitionTerm -> TODO()

            else -> throw UnsupportedOperationException("Unsupported namespace entry term: $term")
        }
    }

    fun evaluateResult(): EvaluationOutcome<Value> = valueThunk.evaluateInitial()

    abstract val valueThunk: Thunk<Value>

    open val expressionMap: ExpressionMap = ExpressionMap.Empty

    open val errors: Set<SemanticError> = emptySet()

    final override val expressionClassification: ExpressionClassification
        get() = ConstClassification(
            constantDefinition = this,
        )
}