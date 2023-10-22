package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import utils.Matcher

class GenericConstructorMatcher(
    private val metaArgumentTypeConstructor: Matcher<TupleType>,
    private val body: Matcher<Expression>,
) : Matcher<GenericConstructor>() {
    override fun match(actual: GenericConstructor) {
        metaArgumentTypeConstructor.match(actual = actual.metaArgumentType)
        body.match(actual = actual.body)
    }
}
