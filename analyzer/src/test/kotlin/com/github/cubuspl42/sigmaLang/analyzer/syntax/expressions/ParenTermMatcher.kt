package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import utils.Matcher

class ParenTermMatcher(
    val wrappedTerm: Matcher<ExpressionTerm>,
) : Matcher<ParenTerm>() {
    override fun match(actual: ParenTerm) {
        wrappedTerm.match(actual = actual.wrappedTerm)
    }
}
