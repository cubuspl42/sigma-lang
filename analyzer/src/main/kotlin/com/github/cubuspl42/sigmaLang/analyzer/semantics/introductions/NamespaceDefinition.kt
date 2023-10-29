package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.QualifiedPath
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Stub
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.analyzer.syntax.DefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.segregatedEntries

class NamespaceStaticBlock(
    private val definitionByName: Map<Identifier, Definition>,
) : StaticBlock() {
    override fun resolveNameLocally(
        name: Symbol,
    ): ResolvedDefinition? = definitionByName[name]?.let {
        ResolvedDefinition(
            definition = it,
        )
    }

    override fun getLocalNames(): Set<Symbol> = definitionByName.keys
}


class NamespaceDefinition(
    private val metaStaticBlock: NamespaceStaticBlock,
    private val staticBlock: NamespaceStaticBlock,
    override val body: UnorderedTupleConstructor,
) : Definition {
    data class InnerScopes(
        val innerMetaScope: StaticScope,
        val innerPrimScope: StaticScope,
    )

    companion object {
        fun build(
            context: Expression.BuildContext,
            qualifiedPath: QualifiedPath,
            term: NamespaceDefinitionTerm,
        ): NamespaceDefinition {
            val segregatedEntries = term.segregatedEntries

            val namespaceDefinition = StaticScope.looped { innerMetaScopeLooped ->
                StaticScope.looped { innerPrimScopeLooped ->
                    val metaDefinitionByName = segregatedEntries.metaDefinitionTerms.associate { metaDefinitionTerm ->
                        metaDefinitionTerm.name to DefinitionTerm.build(
                            context = Expression.BuildContext(
                                outerMetaScope = BuiltinScope,
                                outerScope = innerMetaScopeLooped
                            ),
                            qualifiedPath = qualifiedPath,
                            term = metaDefinitionTerm,
                        )
                    }

                    val primDefinitionByName = segregatedEntries.primDefinitionTerms.associate { definitionTerm ->
                        definitionTerm.name to DefinitionTerm.build(
                            context = Expression.BuildContext(
                                outerMetaScope = innerMetaScopeLooped,
                                outerScope = innerPrimScopeLooped,
                            ),
                            qualifiedPath = qualifiedPath,
                            term = definitionTerm,
                        )
                    }

                    val metaStaticBlock = NamespaceStaticBlock(
                        definitionByName = metaDefinitionByName,
                    )

                    val innerMetaScope = metaStaticBlock.chainWith(
                        outerScope = context.outerMetaScope,
                    )

                    val primStaticBlock = NamespaceStaticBlock(
                        definitionByName = primDefinitionByName,
                    )

                    val innerPrimScope = primStaticBlock.chainWith(
                        outerScope = context.outerScope,
                    )

                    val namespaceBody = object : UnorderedTupleConstructor() {
                        override val term: UnorderedTupleConstructorTerm? = null

                        override val entries: Set<Entry> = primDefinitionByName.entries.map { (name, definition) ->
                            object : Entry() {
                                override val name: Symbol = name

                                override val value: Expression by lazy { definition.bodyStub.resolved }
                            }
                        }.toSet()

                        override val outerScope: StaticScope = innerPrimScopeLooped
                    }

                    val namespaceDefinition = NamespaceDefinition(
                        metaStaticBlock = metaStaticBlock,
                        staticBlock = primStaticBlock,
                        body = namespaceBody,
                    )

                    Pair(
                        Pair(namespaceDefinition, innerMetaScope),
                        innerPrimScope,
                    )
                }.first
            }.first

            return namespaceDefinition
        }
    }

    fun getMetaDefinition(
        name: Symbol,
    ): Definition? = metaStaticBlock.resolveNameLocally(name = name)?.definition

    fun getDefinition(
        name: Symbol,
    ): Definition? = staticBlock.resolveNameLocally(name = name)?.definition

//    override val name: Identifier
//        get() = TODO()

    override val bodyStub: Stub<Expression> = Stub.of(body)
}
