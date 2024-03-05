package com.github.cubuspl42.sigmaLang.core.values

import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.withValue

class ExpressedAbstraction(
    private val abstractionConstructor: AbstractionConstructor,
    private val closure: DynamicScope,
) : Abstraction() {
    override fun compute(argument: Value): Value = abstractionConstructor.body.bind(
        scope = closure.withValue(
            wrapper = abstractionConstructor,
            value = argument,
        ),
    ).value
}
