package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedName
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Definition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.TypeVariableDefinition

abstract class TupleType : TableType() {
    class TypeVariableBlock(
        private val typeVariableDefinitions: Set<TypeVariableDefinition>,
    ) : StaticBlock() {
        override fun resolveNameLocally(
            name: Symbol,
        ): ResolvedName? = TODO()

        override fun getLocalNames(): Set<Symbol> = TODO()
    }

    abstract class Entry {
        abstract val name: Symbol?

        abstract val typeThunk: Thunk<TypeAlike>

        val type: TypeAlike
            get() = typeThunk.value ?: throw IllegalStateException("Unable to evaluate the type thunk")
    }

    class TypePlaceholderBlock(
        private val typeVariableBlock: TypeVariableBlock,
    ) : StaticBlock() {
        override fun resolveNameLocally(name: Symbol): ResolvedName? {
            TODO()
//            return typeVariableBlock.resolveNameLocally(name = name)?.let {
//                Definition(
//                    name = name,
//                    body = AtomicExpression.forType(
//                        TypePlaceholder(
//                            typeVariable = it.typeVariable,
//                        )
//                    )
//                )
//            }
        }

        override fun getLocalNames(): Set<Symbol> = typeVariableBlock.getLocalNames()
    }

    fun buildTypeVariableBlock(): StaticBlock = StaticBlock.Fixed(
        resolvedNameByName = entries.mapNotNull {
            it.name?.let { name ->
                val type = it.type as Type

                type.buildVariableExpression(
                    context = VariableExpressionBuildingContext.Empty,
                )?.let { expression ->
                    name to ResolvedDefinition(
                        definition = Definition(
                            body = expression,
                        )
                    )
                }
            }
        }.toMap()
    )

    override fun buildVariableExpressionDirectly(
        context: VariableExpressionBuildingContext,
    ): Expression = UnorderedTupleConstructor(
        entriesLazy = lazy {
            entries.mapNotNull { entry ->
                val name = entry.name ?: return@mapNotNull null

                val expression = (entry.type as Type).buildVariableExpression(
                    context = context,
                ) ?: return@mapNotNull null

                UnorderedTupleConstructor.Entry(
                    name = name,
                    value = expression,
                )
            }.toSet()
        },
    )

    abstract val entries: Collection<Entry>

    abstract override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike>

    abstract fun buildTypeVariableDefinitions(): Set<TypeVariableDefinition>
}
