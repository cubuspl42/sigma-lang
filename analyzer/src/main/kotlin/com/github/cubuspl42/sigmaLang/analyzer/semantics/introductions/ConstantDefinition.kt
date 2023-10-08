package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.QualifiedPath
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ExpressionMap
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ClassDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ConstantDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntryTerm

abstract class ConstantDefinition : Definition {
    companion object {
        fun build(
            outerScope: StaticScope,
            qualifiedPath: QualifiedPath,
            term: NamespaceEntryTerm,
        ): ConstantDefinition {
            val extendedQualifiedPath = qualifiedPath.extend(term.name)

            return when (term) {
                is ConstantDefinitionTerm -> UserConstantDefinition.build(
                    outerScope = outerScope,
                    term = term,
                )

                is ClassDefinitionTerm -> ClassDefinition.build(
                    outerScope = outerScope,
                    qualifiedPath = extendedQualifiedPath,
                    term = term,
                )

                is NamespaceDefinitionTerm -> TODO()

                else -> throw UnsupportedOperationException("Unsupported namespace entry term: $term")
            }
        }
    }

    open val expressionMap: ExpressionMap = ExpressionMap.Empty

    open val errors: Set<SemanticError> = emptySet()

    fun getValueThunk(): Thunk<Value> {
        val classifiedValue = body.classifiedValue as? ConstClassificationContext<Value> ?: throw IllegalStateException(
            "Const definition body is not constant"
        )

        return classifiedValue.valueThunk
    }
}
