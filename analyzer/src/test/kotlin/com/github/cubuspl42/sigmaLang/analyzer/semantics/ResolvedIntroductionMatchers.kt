package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor
import utils.Matcher

class ResolvedIntroductionMatchers {
    class ResolvedAbstractionArgumentMatcher(
        private val argumentDeclaration: Matcher<AbstractionConstructor.ArgumentDeclaration>,
    ) : Matcher<ResolvedAbstractionArgument>() {
        override fun match(actual: ResolvedAbstractionArgument) {
            argumentDeclaration.match(actual.argumentDeclaration)
        }
    }
}
