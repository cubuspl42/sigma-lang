package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import utils.Matcher

class OrderedTupleConstructorMatcher(
    private val elements: Matcher<List<Expression>>,
) : Matcher<OrderedTupleConstructor>() {
    override fun match(actual: OrderedTupleConstructor) {
        elements.match(actual = actual.elements)
    }
}
