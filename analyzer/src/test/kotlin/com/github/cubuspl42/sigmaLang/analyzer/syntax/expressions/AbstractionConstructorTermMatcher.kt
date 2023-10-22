package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import utils.Matcher

class AbstractionConstructorTermMatcher(
    val argumentType: Matcher<TupleTypeConstructorTerm>,
    val declaredImageType: Matcher<ExpressionTerm?>,
    val image: Matcher<ExpressionTerm>
) : Matcher<AbstractionConstructorTerm>() {
    override fun match(actual: AbstractionConstructorTerm) {
        argumentType.match(actual = actual.argumentType)
        declaredImageType.match(actual = actual.declaredImageType)
        image.match(actual = actual.image)
    }
}
