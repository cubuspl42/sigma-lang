package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ConstantDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Introduction
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

abstract class TupleType : TableType() {
    class TypeVariableDefinition(
        override val name: Symbol,
    ) : ConstantDefinition() {
        override val bodyStub: Expression.Stub<Expression>
            get() = Expression.Stub.of(
                object : Expression() {
                    override val outerScope: StaticScope
                        get() = TODO("Not yet implemented")
                    override val term: ExpressionTerm?
                        get() = TODO("Not yet implemented")
                    override val computedDiagnosedAnalysis: Computation<DiagnosedAnalysis?>
                        get() = TODO("Not yet implemented")
                    override val classifiedValue: ClassificationContext<Value>
                        get() = TODO("Not yet implemented")
                    override val subExpressions: Set<Expression>
                        get() = TODO("Not yet implemented")

                    override fun bind(dynamicScope: DynamicScope): Thunk<Value> {
                        TODO("Not yet implemented")
                    }
                }
            )
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
