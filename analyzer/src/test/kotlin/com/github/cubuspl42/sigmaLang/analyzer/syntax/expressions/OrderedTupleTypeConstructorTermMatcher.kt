package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import utils.Matcher
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier

class OrderedTupleTypeConstructorTermMatcher(
    val elements: Matcher<List<OrderedTupleTypeConstructorTerm.Element>>,
) : Matcher<OrderedTupleTypeConstructorTerm>() {
    class ElementMatcher(
        val name: Matcher<Identifier?>,
        val type: Matcher<ExpressionTerm>,
    ): Matcher<OrderedTupleTypeConstructorTerm.Element>() {
        override fun match(actual: OrderedTupleTypeConstructorTerm.Element) {
            name.match(actual = actual.name)
            type.match(actual = actual.type)
        }
    }

    override fun match(actual: OrderedTupleTypeConstructorTerm) {
        elements.match(actual = actual.elements)
    }
}
