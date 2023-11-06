package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import utils.Matcher

class MethodCallTermMatcher(
    val self: Matcher<ExpressionTerm>,
    val method: Matcher<ReferenceTerm>,
    val argument: Matcher<ExpressionTerm>,
) : Matcher<MethodCallTerm>() {
    override fun match(actual: MethodCallTerm) {
        self.match(actual = actual.self)
        method.match(actual = actual.method)
        argument.match(actual = actual.argument)
    }
}
