package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor

abstract class TupleType : TableType() {
    abstract fun toArgumentDeclarationBlock(): AbstractionConstructor.ArgumentStaticBlock

    abstract override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): TupleType

    abstract fun toArgumentScope(argument: DictValue): DynamicScope
}
