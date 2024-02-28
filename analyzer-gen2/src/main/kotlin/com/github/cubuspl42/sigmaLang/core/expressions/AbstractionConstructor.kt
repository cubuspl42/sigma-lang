package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.values.Abstraction
import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.values.Value

class AbstractionConstructor(
    val body: Expression,
) : Expression() {
    override fun bind(scope: DynamicScope): Lazy<Value> = lazyOf(
        Abstraction(
            abstractionConstructor = this,
            closure = scope,
        )
    )
}
