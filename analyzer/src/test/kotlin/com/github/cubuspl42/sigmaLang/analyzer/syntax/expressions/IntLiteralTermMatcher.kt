package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import utils.Matcher

class IntLiteralTermMatcher(
    private val value: Matcher<IntValue>,
) : Matcher<IntLiteralTerm>() {
    override fun match(actual: IntLiteralTerm) {
        value.match(actual = actual.value)
    }
}
