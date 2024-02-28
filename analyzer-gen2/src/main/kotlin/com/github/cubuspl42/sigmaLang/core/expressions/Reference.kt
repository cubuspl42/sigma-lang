package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.values.Value

class Reference(
    private val referredAbstractionLazy: Lazy<AbstractionConstructor>,
) : Expression() {
    private val referredAbstraction by referredAbstractionLazy

    override fun bind(scope: DynamicScope): Lazy<Value> = lazy {
        scope.getArgumentValue(referredAbstraction)
    }
}
