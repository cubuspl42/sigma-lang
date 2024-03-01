package com.github.cubuspl42.sigmaLang.core.values

interface Callable {
    fun call(argument: Value): Value
}
