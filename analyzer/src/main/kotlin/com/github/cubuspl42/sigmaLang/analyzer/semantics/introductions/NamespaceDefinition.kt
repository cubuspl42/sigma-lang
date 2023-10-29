package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.LeveledResolvedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.QualifiedPath
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Stub
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.analyzer.syntax.DefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntryTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorTerm

class NamespaceStaticBlock(
    private val introductionByName: Map<Identifier, LeveledResolvedIntroduction>,
) : StaticBlock() {
    override fun resolveNameLocally(
        name: Symbol,
    ): LeveledResolvedIntroduction? = introductionByName[name]

    override fun getLocalNames(): Set<Symbol> = introductionByName.keys
}


class NamespaceDefinition(
    private val staticBlock: NamespaceStaticBlock,
    override val body: UnorderedTupleConstructor,
) : Definition {
    companion object {
        fun build(
            context: Expression.BuildContext,
            qualifiedPath: QualifiedPath,
            term: NamespaceDefinitionTerm,
        ): NamespaceDefinition {
            val namespaceDefinition =
                StaticScope.looped { innerScopeLooped ->
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

                    val namespaceDefinition = NamespaceDefinition(
                        staticBlock = staticBlock,
                        body = namespaceBody,
                    )

                    Pair(
                        namespaceDefinition,
                        innerScope,
                    )
                }.first

            return namespaceDefinition
        }
    }

    fun getDefinition(
        name: Symbol,
    ): Definition? {
        val resolvedDefinition = staticBlock.resolveNameLocally(
            name = name,
        )?.resolvedIntroduction as? ResolvedDefinition

        return resolvedDefinition?.definition
    }

//    override val name: Identifier
//        get() = TODO()

    override val bodyStub: Stub<Expression> = Stub.of(body)
}
