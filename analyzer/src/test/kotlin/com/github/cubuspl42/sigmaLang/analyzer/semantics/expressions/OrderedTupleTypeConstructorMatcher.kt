package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import utils.Matcher

class OrderedTupleTypeConstructorMatcher(
    private val elements: Matcher<List<OrderedTupleTypeConstructor.Element>>,
) : Matcher<OrderedTupleTypeConstructor>() {
    class ElementMatcher(
        private val name: Matcher<Identifier?>,
        private val type: Matcher<Expression>,
    ) : Matcher<OrderedTupleTypeConstructor.Element>() {
        override fun match(actual: OrderedTupleTypeConstructor.Element) {
            name.match(actual = actual.name)
            type.match(actual = actual.type)
        }
    }

    override fun match(actual: OrderedTupleTypeConstructor) {
        elements.match(actual = actual.elements)
    }
}
