package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorTerm
import utils.Matcher

object AbstractionConstructorMatchers {
    class ArgumentDeclarationMatcher(
        private val declaredType: Matcher<TupleType>,
    ) : Matcher<AbstractionConstructorTerm.ArgumentDeclaration>() {
        override fun match(actual: AbstractionConstructorTerm.ArgumentDeclaration) {
            declaredType.match(actual.declaredType)
        }
    }
}
