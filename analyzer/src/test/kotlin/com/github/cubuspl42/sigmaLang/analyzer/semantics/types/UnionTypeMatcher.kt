package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import utils.Matcher

class UnionTypeMatcher(
    private val memberTypes: Matcher<Set<TypeAlike>>,
) : Matcher<UnionType>() {
    override fun match(actual: UnionType) {
        memberTypes.match(actual = actual.memberTypes)
    }
}
