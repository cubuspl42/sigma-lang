package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import utils.Matcher

class GenericConstructorTermMatcher(
    val metaArgument: Matcher<TupleTypeConstructorTerm>,
    val body: Matcher<ExpressionTerm>,
) : Matcher<GenericConstructorTerm>() {
    override fun match(actual: GenericConstructorTerm) {
        metaArgument.match(actual = actual.metaArgumentType)
        body.match(actual = actual.body)
    }
}
