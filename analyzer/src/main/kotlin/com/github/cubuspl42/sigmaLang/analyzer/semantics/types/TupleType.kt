package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Abstraction

abstract class TupleType : TableType() {
    abstract fun toArgumentDeclarationBlock(): Abstraction.ArgumentStaticBlock

    abstract override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): TupleType

    abstract fun toArgumentScope(argument: DictValue): Scope
}
