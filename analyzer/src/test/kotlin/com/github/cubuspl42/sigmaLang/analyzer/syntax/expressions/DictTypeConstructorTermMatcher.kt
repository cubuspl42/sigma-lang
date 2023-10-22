package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import utils.Matcher

class DictTypeConstructorTermMatcher(
    val keyType: Matcher<ExpressionTerm>,
    val valueType: Matcher<ExpressionTerm>
) : Matcher<DictTypeConstructorTerm>() {

    override fun match(actual: DictTypeConstructorTerm) {
        keyType.match(actual = actual.keyType)
        valueType.match(actual = actual.valueType)
    }
}
