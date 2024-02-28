package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.values.Value

abstract class Expression {
    abstract fun bind(
        scope: DynamicScope,
    ): Lazy<Value>
}
