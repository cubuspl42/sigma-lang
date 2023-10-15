package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.QualifiedPath
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ClassDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ConstantDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntryTerm

object ConstantDefinition {
    fun build(
        context: Expression.BuildContext,
        qualifiedPath: QualifiedPath,
        term: NamespaceEntryTerm,
    ): Definition {
        val extendedQualifiedPath = qualifiedPath.extend(term.name)

        return when (term) {
            is ConstantDefinitionTerm -> UserVariableDefinition.build(
                context = context,
                term = term,
            )

            is ClassDefinitionTerm -> ClassDefinition.build(
                context = context,
                qualifiedPath = extendedQualifiedPath,
                term = term,
            )

            is NamespaceDefinitionTerm -> TODO()

            else -> throw UnsupportedOperationException("Unsupported namespace entry term: $term")
        }
    }
}
