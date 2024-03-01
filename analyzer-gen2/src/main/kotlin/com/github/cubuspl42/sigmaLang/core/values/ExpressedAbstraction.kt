package com.github.cubuspl42.sigmaLang.core.values

import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor

class ExpressedAbstraction(
    private val abstractionConstructor: AbstractionConstructor,
    private val closure: DynamicScope,
): Abstraction() {
    override fun compute(argument: Value): Value = abstractionConstructor.body.bind(
        scope = closure.extend(
            abstractionConstructor = abstractionConstructor,
            value = argument,
        ),
    ).value
}
