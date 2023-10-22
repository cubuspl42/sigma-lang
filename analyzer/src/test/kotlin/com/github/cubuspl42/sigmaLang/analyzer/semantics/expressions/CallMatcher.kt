package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike
import utils.Matcher

class CallMatcher(
    private val subject: Matcher<Expression>,
    private val argument: Matcher<Expression>,
) : Matcher<Call>() {
    class NonFullyInferredCalleeTypeErrorMatcher(
        val calleeGenericType: Matcher<TypeAlike>,
        val unresolvedPlaceholders: Matcher<Set<TypeAlike>>,
    ) : Matcher<Call.NonFullyInferredCalleeTypeError>() {
        override fun match(actual: Call.NonFullyInferredCalleeTypeError) {
            calleeGenericType.match(actual = actual.calleeGenericType)
            unresolvedPlaceholders.match(actual = actual.unresolvedPlaceholders)
        }
    }

    override fun match(actual: Call) {
        subject.match(actual = actual.subject)
        argument.match(actual = actual.argument)
    }
}
