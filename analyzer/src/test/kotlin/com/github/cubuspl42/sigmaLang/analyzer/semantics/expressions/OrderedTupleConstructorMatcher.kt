package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import utils.ListMatchers
import utils.Matcher

class OrderedTupleConstructorMatcher(
    private val elements: Matcher<List<Expression>>,
) : Matcher<OrderedTupleConstructor>() {
    companion object {
        fun withElementsInOrder(
            vararg elements: Matcher<Expression>,
        ): Matcher<OrderedTupleConstructor> = OrderedTupleConstructorMatcher(
            elements = ListMatchers.inOrder(*elements),
        )
    }

    override fun match(actual: OrderedTupleConstructor) {
        elements.match(actual = actual.elements)
    }
}
