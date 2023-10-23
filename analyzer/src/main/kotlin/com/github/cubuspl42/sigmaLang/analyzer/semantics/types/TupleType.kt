package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedName
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor.ArgumentDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AtomicExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
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

    fun buildTypeVariableBlock(): TypeVariableBlock = TypeVariableBlock(
        typeVariableDefinitions = buildTypeVariableDefinitions(),
    )

    abstract override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike>

    abstract fun buildTypeVariableDefinitions(): Set<TypeVariableDefinition>
}
