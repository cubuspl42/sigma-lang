package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.LeveledResolvedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.QualifiedPath
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.asStub
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.UserVariableDefinition

sealed interface NamespaceEntryTerm {
    companion object {
        fun build(
            context: Expression.BuildContext,
            qualifiedPath: QualifiedPath,
            term: NamespaceEntryTerm,
        ): LeveledResolvedIntroduction {
            val extendedQualifiedPath = qualifiedPath.extend(term.name)

            return when (term) {
                is ConstantDefinitionTerm -> LeveledResolvedIntroduction(
                    level = StaticScope.Level.Primary,
                    resolvedIntroduction = UserVariableDefinition.build(
                        context = context,
                        term = term,
                    )
                )

                is MetaDefinitionTerm -> LeveledResolvedIntroduction(
                    level = StaticScope.Level.Meta,
                    resolvedIntroduction = UserVariableDefinition.build(
                        context = Expression.BuildContext(
                            outerScope = context.outerScope
                        ), // TODO: Shift
                        term = term,
                    )
                )

                is ClassDefinitionTerm -> LeveledResolvedIntroduction(
                    level = StaticScope.Level.Meta,
                    resolvedIntroduction = ResolvedDefinition(
                        bodyLazy = ClassDefinition.build(
                            context = context,
                            qualifiedPath = extendedQualifiedPath,
                            term = term,
                        ).classBodyLazy,
                    )
                )

                is NamespaceDefinitionTerm -> TODO()

                else -> throw UnsupportedOperationException("Unsupported namespace entry term: $term")
            }
        }
    }

    val name: Identifier
}
