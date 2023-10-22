package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import utils.Matcher

class IsUndefinedCheckTermMatcher(
    val argument: Matcher<ExpressionTerm>
) : Matcher<IsUndefinedCheckTerm>() {
    override fun match(actual: IsUndefinedCheckTerm) {
        argument.match(actual = actual.argument)
    }
}
