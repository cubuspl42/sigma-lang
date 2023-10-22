package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import utils.Matcher

class OrderedTupleConstructorTermMatcher(
    val elements: Matcher<List<ExpressionTerm>>,
) : Matcher<OrderedTupleConstructorTerm>() {
    override fun match(actual: OrderedTupleConstructorTerm) {
        elements.match(actual = actual.elements)
    }
}
