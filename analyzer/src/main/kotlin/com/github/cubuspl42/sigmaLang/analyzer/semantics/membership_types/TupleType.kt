package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ClassifiedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ConstantDefinition

abstract class TupleType : TableType() {
    class TypeVariableDefinition(
        override val name: Identifier,
    ) : ConstantDefinition() {
        override val valueThunk: Thunk<Value> = Thunk.pure(
            TypeVariable(
                formula = Formula(name = name),
            ).asValue
        )

        override val computedEffectiveType: Expression.Computation<MembershipType> =
            Expression.Computation.pure(TypeType)
    }

    inner class MetArgumentTupleBlock : StaticBlock() {
        override fun resolveNameLocally(
            name: Identifier,
        ): ClassifiedIntroduction? = typeVariableDefinitions.find { it.name == name }

        override fun getLocalNames(): Set<Identifier> = typeVariableDefinitions.map { it.name }.toSet()
    }

    fun toMetaArgumentDeclarationBlock() = MetArgumentTupleBlock()

    abstract fun toArgumentDeclarationBlock(): AbstractionConstructor.ArgumentStaticBlock

    abstract override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): TupleType

    abstract fun toArgumentScope(argument: DictValue): DynamicScope

    abstract val typeVariableDefinitions: Set<TypeVariableDefinition>
}
