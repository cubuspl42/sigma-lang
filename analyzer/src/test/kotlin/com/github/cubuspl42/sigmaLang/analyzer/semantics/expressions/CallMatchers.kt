package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeAlike
import utils.Matcher

class CallMatchers {
    class NonFullyInferredCalleeTypeErrorMatcher(
        val calleeGenericType: Matcher<TypeAlike>,
        val unresolvedPlaceholders: Matcher<Set<TypeAlike>>,
    ) : Matcher<Call.NonFullyInferredCalleeTypeError>() {
        override fun match(actual: Call.NonFullyInferredCalleeTypeError) {
            calleeGenericType.match(actual = actual.calleeGenericType)
            unresolvedPlaceholders.match(actual = actual.unresolvedPlaceholders)
        }
    }
}
