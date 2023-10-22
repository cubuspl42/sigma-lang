package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import utils.Matcher

class SetConstructorTermMatcher(
    val elements: Matcher<List<ExpressionTerm>>,
) : Matcher<SetConstructorTerm>() {
    override fun match(actual: SetConstructorTerm) {
        elements.match(actual = actual.elements)
    }
}
