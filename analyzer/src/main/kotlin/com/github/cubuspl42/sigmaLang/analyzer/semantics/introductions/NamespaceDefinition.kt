package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.QualifiedPath
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntryTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorTerm

class NamespaceStaticBlock(
    private val definitionByName: Map<Identifier, Definition>,
) : StaticBlock() {
    override fun resolveNameLocally(
        name: Symbol,
    ): Definition? = definitionByName[name]

    override fun getLocalNames(): Set<Symbol> = definitionByName.keys
}


class NamespaceDefinition(
    override val name: Symbol,
    private val entryBodyByName: Map<Identifier, Definition>,
    private val staticBlock: NamespaceStaticBlock,
    override val body: UnorderedTupleConstructor,
) : Definition {
    companion object {
        fun build(
            context: Expression.BuildContext,
            qualifiedPath: QualifiedPath,
            term: NamespaceDefinitionTerm,
        ): NamespaceDefinition {
            val (namespaceDefinition, _) = StaticScope.looped { innerDeclarationScopeLooped ->
                val definitionByName = term.namespaceEntries.associate { entryTerm ->
                    entryTerm.name to ConstantDefinition.build(
                        context = context.copy(
                            outerScope = innerDeclarationScopeLooped,
                        ),
                        qualifiedPath = qualifiedPath,
                        term = entryTerm,
                    )
                }

                val staticBlock = NamespaceStaticBlock(
                    definitionByName = definitionByName,
                )

                val innerDeclarationScope = staticBlock.chainWith(
                    outerScope = context.outerScope,
                )

                val namespaceBody = object : UnorderedTupleConstructor() {
                    override val term: UnorderedTupleConstructorTerm? = null

                    override val entries: Set<Entry> = definitionByName.entries.map { (name, definition) ->
                        object : Entry() {
                            override val name: Symbol = name

                            override val value: Expression by lazy { definition.bodyStub.resolved }
                        }
                    }.toSet()

                    override val outerScope: StaticScope = context.outerScope
                }

                val namespaceDefinition = NamespaceDefinition(
                    name = term.name,
                    entryBodyByName = definitionByName,
                    staticBlock = staticBlock,
                    body = namespaceBody,
                )

                return@looped Pair(namespaceDefinition, innerDeclarationScope)
            }

            return namespaceDefinition
        }
    }

    fun getDefinition(
        name: Symbol,
    ): Definition? = staticBlock.resolveNameLocally(name = name)

//    override val name: Identifier
//        get() = TODO()

    override val bodyStub: Expression.Stub<Expression> = Expression.Stub.of(body)
}
