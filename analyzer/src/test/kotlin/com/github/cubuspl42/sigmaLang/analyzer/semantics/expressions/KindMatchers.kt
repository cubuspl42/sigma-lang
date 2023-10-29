package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import utils.Matcher

data class GenericTypeMatcher(
    val parameterType: Matcher<TupleType>,
    val bodyType: Matcher<Type>,
) : Matcher<GenericType>() {
    override fun match(actual: GenericType) {
        parameterType.match(actual = actual.parameterType)
        bodyType.match(actual = actual.bodyType)
    }
}
