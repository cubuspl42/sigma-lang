package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Definition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Introduction
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

abstract class TupleType : TableType() {
    class TypeVariableDefinition(
        override val name: Symbol,
    ) : Definition {
        override val bodyStub: Expression.Stub<Expression> = Expression.Stub.of(
            object : Expression() {
                override val outerScope: StaticScope = StaticScope.Empty

                override val term: ExpressionTerm? = null

                override val computedDiagnosedAnalysis: Computation<DiagnosedAnalysis?> = Computation.pure(
                    DiagnosedAnalysis(
                        analysis = Analysis(inferredType = TypeType),
                        directErrors = emptySet(),
                    ),
                )

                override val subExpressions: Set<Expression> = emptySet()

                override fun bindDirectly(dynamicScope: DynamicScope): Thunk<Value> = Thunk.pure(
                    TypeVariable(
                        formula = Formula(name = name),
                    ).asValue
                )
            },
        )
    }

    inner class MetaArgumentTupleBlock : StaticBlock() {
        override fun resolveNameLocally(
            name: Symbol,
        ): Introduction? = typeVariableDefinitions.find { it.name == name }

        override fun getLocalNames(): Set<Symbol> = typeVariableDefinitions.map { it.name }.toSet()
    }

    fun toMetaArgumentDeclarationBlock() = MetaArgumentTupleBlock()

    abstract fun toArgumentDeclarationBlock(): AbstractionConstructor.ArgumentStaticBlock

    abstract override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): TupleType

    abstract fun toArgumentScope(argument: DictValue): DynamicScope

    abstract val typeVariableDefinitions: Set<TypeVariableDefinition>
}
