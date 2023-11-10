package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import utils.ListMatchers
import utils.Matcher

class OrderedTupleConstructorTermMatcher(
    val elements: Matcher<List<ExpressionTerm>>,
) : Matcher<OrderedTupleConstructorTerm>() {
    companion object {
        fun withElementsInOrder(
            vararg elements: Matcher<ExpressionTerm>,
        ): Matcher<OrderedTupleConstructorTerm> = OrderedTupleConstructorTermMatcher(
            elements = ListMatchers.inOrder(*elements),
        )
    }

    override fun match(actual: OrderedTupleConstructorTerm) {
        elements.match(actual = actual.elements)
    }
}
