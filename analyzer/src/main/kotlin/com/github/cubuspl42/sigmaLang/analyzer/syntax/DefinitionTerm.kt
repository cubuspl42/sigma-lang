package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.QualifiedPath
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Definition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.UserVariableDefinition
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

sealed interface DefinitionTerm {
    companion object {
        fun build(
            context: Expression.BuildContext,
            qualifiedPath: QualifiedPath,
            term: DefinitionTerm,
        ): Definition {
            val extendedQualifiedPath = qualifiedPath.extend(term.name)

            return when (term) {
                is ConstantDefinitionTerm -> UserVariableDefinition.build(
                    context = context,
                    term = term,
                )

                is MetaDefinitionTerm -> UserVariableDefinition.build(
                    context = Expression.BuildContext(
                        outerMetaScope = BuiltinScope,
                        outerScope = context.outerMetaScope,
                    ),
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

    val name: Identifier
    val declaredTypeBody: ExpressionTerm?
    val body: ExpressionTerm
}
