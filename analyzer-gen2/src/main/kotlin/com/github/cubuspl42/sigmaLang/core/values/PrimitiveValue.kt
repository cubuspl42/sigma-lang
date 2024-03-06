package com.github.cubuspl42.sigmaLang.core.values

import com.github.cubuspl42.sigmaLang.core.expressions.Literal

abstract class PrimitiveValue : Value() {
    abstract fun toLiteral(): Literal
}
