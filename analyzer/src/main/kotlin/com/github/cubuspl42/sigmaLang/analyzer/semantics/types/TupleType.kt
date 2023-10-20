package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AtomicExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Definition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.TypeVariableDefinition

abstract class TupleType : TableType() {

    class TypeVariableBlock(
        private val typeVariableDefinitions: Set<TypeVariableDefinition>,
    ) : StaticBlock() {
        override fun resolveNameLocally(
            name: Symbol,
        ): TypeVariableDefinition? = typeVariableDefinitions.find { it.name == name }

        override fun getLocalNames(): Set<Symbol> = typeVariableDefinitions.map { it.name }.toSet()
    }


    class TypePlaceholderBlock(
        private val typeVariableBlock: TypeVariableBlock,
    ) : StaticBlock() {
        override fun resolveNameLocally(name: Symbol): Definition? =
            typeVariableBlock.resolveNameLocally(name = name)?.let {
                Definition(
                    name = name,
                    body = AtomicExpression.forType(
                        TypePlaceholder(
                            typeVariable = it.typeVariable,
                        )
                    )
                )
            }

        override fun getLocalNames(): Set<Symbol> = typeVariableBlock.getLocalNames()
    }

    fun buildTypeVariableBlock(): TypeVariableBlock = TypeVariableBlock(
        typeVariableDefinitions = buildTypeVariableDefinitions(),
    )


    abstract fun toArgumentDeclarationBlock(): AbstractionConstructor.ArgumentStaticBlock

    abstract override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike>

    abstract fun toArgumentScope(argument: DictValue): DynamicScope

    abstract fun buildTypeVariableDefinitions(): Set<TypeVariableDefinition>
}
