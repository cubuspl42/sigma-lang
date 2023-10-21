package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import utils.Matcher

data class KindConstructorMatcher(
    val metaArgumentType: Matcher<TupleType>,
) : Matcher<GenericType>() {
    override fun match(actual: GenericType) {
        metaArgumentType.match(actual = actual.metaArgumentType)
    }
}
