package com.github.cubuspl42.sigmaLang.core.values

abstract class CallableValue : Value() {
    abstract fun call(argument: Value): Value
}
