package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import utils.Matcher


class PostfixCallTermMatcher(
    private val subject: Matcher<ExpressionTerm>,
    private val argument: Matcher<ExpressionTerm>,
) : Matcher<PostfixCallTerm>() {
    override fun match(actual: PostfixCallTerm) {
        subject.match(actual = actual.subject)
        argument.match(actual = actual.argument)
    }
}
