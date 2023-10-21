package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike
import utils.Matcher

class AbstractionConstructorMatcher(
    private val argumentType: Matcher<TupleType>,
    private val declaredImageType: Matcher<TypeAlike?>,
    private val image: Matcher<Expression>,
) : Matcher<AbstractionConstructor>() {
    override fun match(actual: AbstractionConstructor) {
        argumentType.match(actual = actual.argumentType)
        declaredImageType.match(actual = actual.declaredImageType)
        image.match(actual = actual.image)
    }
}
