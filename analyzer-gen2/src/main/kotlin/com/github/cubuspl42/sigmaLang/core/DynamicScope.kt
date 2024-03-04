package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.KnotConstructor
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value

data class DynamicScope(
    private val argumentValueByAbstraction: Map<AbstractionConstructor, Value>,
    private val knotValueByKnot: Map<KnotConstructor, UnorderedTuple>,
) {
    companion object {
        val Empty = DynamicScope(
            argumentValueByAbstraction = emptyMap(),
            knotValueByKnot = emptyMap(),
        )
    }

    fun getArgumentValue(abstractionConstructor: AbstractionConstructor): Value =
        argumentValueByAbstraction.getOrElse(abstractionConstructor) {
            throw IllegalArgumentException("No value for abstraction $abstractionConstructor")
        }

    fun getKnotValue(knotConstructor: KnotConstructor): UnorderedTuple =
        knotValueByKnot.getOrElse(knotConstructor) {
            throw IllegalArgumentException("No value for knot $knotConstructor")
        }

    fun withWrappingAbstraction(
        abstractionConstructor: AbstractionConstructor,
        value: Value,
    ): DynamicScope = copy(
        argumentValueByAbstraction = argumentValueByAbstraction + (abstractionConstructor to value),
    )

    fun withWrappingKnot(
        knotConstructor: KnotConstructor,
        value: UnorderedTuple,
    ): DynamicScope = copy(
        knotValueByKnot = knotValueByKnot + (knotConstructor to value),
    )
}
