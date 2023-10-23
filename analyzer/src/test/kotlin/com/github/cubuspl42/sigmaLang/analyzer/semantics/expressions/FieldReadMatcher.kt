package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import utils.Matcher

class FieldReadMatcher(
    private val subject: Matcher<Expression>,
    private val fieldName: Matcher<Symbol>,
) : Matcher<FieldRead>() {
    override fun match(actual: FieldRead) {
        subject.match(actual = actual.subject)
        fieldName.match(actual = actual.fieldName)
    }
}
