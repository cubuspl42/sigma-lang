package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import utils.ListMatchers
import utils.Matcher
import utils.match

class OrderedTupleTypeMatcher(
    val elements: Matcher<List<OrderedTupleType.IndexedElement>>,
) : Matcher<OrderedTupleType>() {
    companion object {
        fun withElementsInOrder(
            vararg elements: ElementMatcher,
        ): Matcher<OrderedTupleType> = OrderedTupleTypeMatcher(
            elements = ListMatchers.inOrder(elements = elements)
        )
    }

    class ElementMatcher(
        val name: Matcher<Identifier?>,
        val type: Matcher<TypeAlike>,
    ) : Matcher<OrderedTupleType.IndexedElement>() {
        override fun match(actual: OrderedTupleType.IndexedElement) {
            name.match(actual = actual.name)
            type.match(actual = actual.type)
        }
    }

    override fun match(actual: OrderedTupleType) {
        elements.match(actual = actual.indexedElements)
    }
}
