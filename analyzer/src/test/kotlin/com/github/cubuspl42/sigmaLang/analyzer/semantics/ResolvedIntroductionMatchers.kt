package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import utils.Matcher

class ResolvedIntroductionMatchers {
    class ResolvedAbstractionArgumentMatcher(
        private val argumentDeclaration: Matcher<AbstractionConstructor.ArgumentDeclaration>,
    ) : Matcher<ResolvedAbstractionArgument>() {
        override fun match(actual: ResolvedAbstractionArgument) {
            argumentDeclaration.match(actual.argumentDeclaration)
        }
    }

    class ResolvedDefinitionMatcher(
        private val body: Matcher<Expression>
    ) : Matcher<ResolvedDefinition>() {
        override fun match(actual: ResolvedDefinition) {
            body.match(actual.body)
        }
    }
}
