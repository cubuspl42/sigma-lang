package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.QualifiedPath
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.NamespaceStaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.StaticScope

interface NamespaceDefinitionTerm : NamespaceEntryTerm {
    data class Analysis(
        val namespaceBodyLazy: Lazy<Expression>,
        val definitionBlockLazy: Lazy<StaticBlock>,
    ) {
        val namespaceBody: Expression by namespaceBodyLazy
        val definitionBlock: StaticBlock by definitionBlockLazy
    }

    companion object {
        fun analyze(
            context: Expression.BuildContext,
            qualifiedPath: QualifiedPath,
            term: NamespaceDefinitionTerm,
        ): Analysis = StaticScope.looped { innerScopeLooped ->
            val definitionByName = term.entries.associate { definitionTerm ->
                definitionTerm.name to NamespaceEntryTerm.build(
                    context = Expression.BuildContext(
                        outerScope = innerScopeLooped,
                    ),
                    qualifiedPath = qualifiedPath,
                    term = definitionTerm,
                )
            }

            val staticBlock = NamespaceStaticBlock(
                introductionByName = definitionByName,
            )

            val innerScope = staticBlock.chainWith(
                outerScope = context.outerScope,
            )

            val namespaceBody = object : UnorderedTupleConstructor() {
                override val term: UnorderedTupleConstructorTerm? = null

                override val entries: Set<Entry> = definitionByName.entries.map { (name, definition) ->
                    object : Entry() {
                        override val name: Symbol = name

                        override val value: Expression by lazy {
                            val resolvedDefinition = definition.resolvedIntroduction as ResolvedDefinition
                            resolvedDefinition.body
                        }
                    }
                }.toSet()

                override val outerScope: StaticScope = innerScopeLooped
            }

            Pair(
                Analysis(
                    namespaceBodyLazy = lazyOf(namespaceBody),
                    definitionBlockLazy = lazyOf(staticBlock),
                ),
                innerScope,
            )
        }.first
    }

    data class SegregatedEntries(
        val primDefinitionTerms: List<DefinitionTerm>,
        val metaDefinitionTerms: List<MetaDefinitionTerm>,
    )

    override val name: Identifier

    val entries: List<NamespaceEntryTerm>

}
