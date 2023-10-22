package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import utils.Matcher

class IfExpressionTermMatcher(
    val guard: Matcher<ExpressionTerm>,
    val trueBranch: Matcher<ExpressionTerm>,
    val falseBranch: Matcher<ExpressionTerm>
) : Matcher<IfExpressionTerm>() {
    override fun match(actual: IfExpressionTerm) {
        guard.match(actual = actual.guard)
        trueBranch.match(actual = actual.trueBranch)
        falseBranch.match(actual = actual.falseBranch)
    }
}
