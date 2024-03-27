package com.github.cubuspl42.sigmaLang.core.values

import com.github.cubuspl42.sigmaLang.core.DynamicContext
import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.withValue

class ExpressedAbstractionValue(
    private val abstractionConstructor: AbstractionConstructor,
    private val closure: DynamicContext,
) : AbstractionValue() {
    override fun compute(argument: Value): Value = abstractionConstructor.body.bind(
        context = closure.withScope {
            withValue(
                wrapper = abstractionConstructor,
                valueLazy = lazyOf(argument),
            )
        },
    ).value
}
