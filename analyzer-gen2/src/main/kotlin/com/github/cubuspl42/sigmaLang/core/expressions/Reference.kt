package com.github.cubuspl42.sigmaLang.core.expressions

class Reference(
    private val referredAbstractionLazy: Lazy<AbstractionConstructor>,
) : Expression() {
    val referredAbstraction by referredAbstractionLazy
}
