package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import utils.Matcher

class IntLiteralTypeMatcher(
    private val value: Matcher<Long>,
) : Matcher<IntLiteralType>() {
    override fun match(actual: IntLiteralType) {
        this.value.match(actual.value.value)
    }
}
