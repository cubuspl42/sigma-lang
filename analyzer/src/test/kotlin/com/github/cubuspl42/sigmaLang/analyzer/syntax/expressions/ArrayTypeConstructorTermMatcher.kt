package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import utils.Matcher


class ArrayTypeConstructorTermMatcher(
    private val elementType: Matcher<ExpressionTerm>,
) : Matcher<ArrayTypeConstructorTerm>() {
    override fun match(actual: ArrayTypeConstructorTerm) {
        elementType.match(actual = actual.elementType)
    }
}
