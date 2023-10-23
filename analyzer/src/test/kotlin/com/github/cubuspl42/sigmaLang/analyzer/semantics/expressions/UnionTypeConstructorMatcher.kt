package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import utils.Matcher

class UnionTypeConstructorMatcher(
    private val types: Matcher<Set<Expression>>,
) : Matcher<UnionTypeConstructor>() {
    override fun match(actual: UnionTypeConstructor) {
        types.match(actual = actual.types)
    }
}
