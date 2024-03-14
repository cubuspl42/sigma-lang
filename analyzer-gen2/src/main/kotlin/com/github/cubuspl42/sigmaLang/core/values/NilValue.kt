package com.github.cubuspl42.sigmaLang.core.values

import com.github.cubuspl42.sigmaLang.core.expressions.Literal

data object NilValue : PrimitiveValue() {
    override fun toLiteral(): Literal {
        TODO("Not yet implemented")
    }
}
