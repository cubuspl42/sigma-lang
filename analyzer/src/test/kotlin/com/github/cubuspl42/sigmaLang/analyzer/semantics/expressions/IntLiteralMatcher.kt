package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import utils.Matcher

class IntLiteralMatcher(
    private val value: Matcher<Long>,
) : Matcher<IntLiteral>() {
    override fun match(actual: IntLiteral) {
        value.match(actual = actual.value.value)
    }
}
