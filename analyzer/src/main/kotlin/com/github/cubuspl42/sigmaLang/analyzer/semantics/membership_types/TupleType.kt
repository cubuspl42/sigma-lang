package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ComputableFunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.QuasiExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ConstantDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Introduction

abstract class TupleType : TableType() {
    class TypeVariableDefinition(
        override val name: Symbol,
    ) : ConstantDefinition() {
        override val body: QuasiExpression = object : QuasiExpression() {
            override val computedAnalysis: Expression.Computation<Expression.Analysis?> by lazy {
                Expression.Computation.pure(
                    Expression.Analysis(inferredType = TypeType),
                )
            }

            override val classifiedValue = ConstClassificationContext.pure(
                TypeVariable(
                    formula = Formula(name = name),
                ).asValue,
            )
        }
    }

    inner class MetArgumentTupleBlock : StaticBlock() {
        override fun resolveNameLocally(
            name: Symbol,
        ): Introduction? = typeVariableDefinitions.find { it.name == name }

        override fun getLocalNames(): Set<Symbol> = typeVariableDefinitions.map { it.name }.toSet()
    }

    fun toMetaArgumentDeclarationBlock() = MetArgumentTupleBlock()

    abstract fun toArgumentDeclarationBlock(): AbstractionConstructor.ArgumentStaticBlock

    abstract override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): TupleType

    abstract fun toArgumentScope(argument: DictValue): DynamicScope

    abstract val typeVariableDefinitions: Set<TypeVariableDefinition>
}
