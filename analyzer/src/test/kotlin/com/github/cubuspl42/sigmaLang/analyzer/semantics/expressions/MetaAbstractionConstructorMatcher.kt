package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import utils.Matcher

class MetaAbstractionConstructorMatcher(
    private val metaArgumentTypeConstructor: Matcher<TupleType>,
    private val body: Matcher<Expression>,
) : Matcher<MetaAbstractionConstructor>() {
    override fun match(actual: MetaAbstractionConstructor) {
        metaArgumentTypeConstructor.match(actual = actual.metaArgumentType)
        body.match(actual = actual.body)
    }
}
