package com.github.cubuspl42.sigmaLang.core.values

abstract class Callable : Value() {
    abstract fun call(argument: Value): Value
}
