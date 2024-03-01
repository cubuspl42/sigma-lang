package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.values.Value

data class Literal(
    val value: Value,
) : Expression() {
    override fun bind(scope: DynamicScope): Lazy<Value> = lazyOf(value)
}
