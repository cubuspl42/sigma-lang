package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import utils.Matcher
import utils.match

class OrderedTupleTypeMatcher(
    val elements: List<ElementMatcher>,
) : Matcher<OrderedTupleType>() {
    class ElementMatcher(
        val name: Matcher<Identifier?>,
        val type: Matcher<TypeAlike>,
    ) : Matcher<OrderedTupleType.Element>() {
        override fun match(actual: OrderedTupleType.Element) {
            name.match(actual = actual.name)
            type.match(actual = actual.type)
        }
    }

    override fun match(actual: OrderedTupleType) {
        elements.match(actual = actual.elements)
    }
}
