package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.values.Value

class DynamicScope(
    private val argumentValueByAbstraction: Map<AbstractionConstructor, Value>,
) {
    companion object {
        val Empty = DynamicScope(
            argumentValueByAbstraction = emptyMap(),
        )
    }

    fun getArgumentValue(abstractionConstructor: AbstractionConstructor): Value {
        return argumentValueByAbstraction.getOrElse(abstractionConstructor) {
            throw IllegalArgumentException("No value for abstraction $abstractionConstructor")
        }
    }

    fun extend(
        abstractionConstructor: AbstractionConstructor,
        value: Value,
    ): DynamicScope = DynamicScope(
        argumentValueByAbstraction = argumentValueByAbstraction + (abstractionConstructor to value),
    )
}
