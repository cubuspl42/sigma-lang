package com.github.cubuspl42.sigmaLang.core.values

abstract class Abstraction : Callable() {
    final override fun call(argument: Value): Value = compute(argument = argument)

    abstract fun compute(argument: Value): Value
}
