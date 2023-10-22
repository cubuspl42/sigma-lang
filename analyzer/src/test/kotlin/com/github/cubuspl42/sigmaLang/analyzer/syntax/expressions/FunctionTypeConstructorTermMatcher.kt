package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import utils.Matcher

class FunctionTypeConstructorTermMatcher(
    val metaArgumentType: Matcher<TupleTypeConstructorTerm?>,
    val argumentType: Matcher<TupleTypeConstructorTerm>,
    val imageType: Matcher<ExpressionTerm>,
) : Matcher<FunctionTypeConstructorTerm>() {
    override fun match(actual: FunctionTypeConstructorTerm) {
        metaArgumentType.match(actual = actual.metaArgumentType)
        argumentType.match(actual = actual.argumentType)
        imageType.match(actual = actual.imageType)
    }
}
