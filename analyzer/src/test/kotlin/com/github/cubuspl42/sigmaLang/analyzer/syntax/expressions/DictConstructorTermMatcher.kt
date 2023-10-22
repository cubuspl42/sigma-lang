package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import utils.Matcher

class DictConstructorTermMatcher(
    val associations: Matcher<List<DictConstructorTerm.Association>>,
) : Matcher<DictConstructorTerm>() {
    class AssociationMatcher(
        val key: Matcher<ExpressionTerm>,
        val value: Matcher<ExpressionTerm>,
    ): Matcher<DictConstructorTerm.Association>() {
        override fun match(actual: DictConstructorTerm.Association) {
            key.match(actual = actual.key)
            value.match(actual = actual.value)
        }
    }

    override fun match(actual: DictConstructorTerm) {
        associations.match(actual = actual.associations)
    }
}
