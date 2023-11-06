package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import utils.Matcher

object AbstractionConstructorMatchers {
    class ArgumentDeclarationMatcher(
        private val declaredType: Matcher<TupleType>,
    ) : Matcher<AbstractionConstructor.ArgumentDeclaration>() {
        override fun match(actual: AbstractionConstructor.ArgumentDeclaration) {
            declaredType.match(actual.declaredType)
        }
    }
}
