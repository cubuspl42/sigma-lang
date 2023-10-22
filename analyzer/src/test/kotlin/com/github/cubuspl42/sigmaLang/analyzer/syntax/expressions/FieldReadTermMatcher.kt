package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import utils.Matcher
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier

class FieldReadTermMatcher(
    val subject: Matcher<ExpressionTerm>,
    val fieldName: Matcher<Identifier>,
) : Matcher<FieldReadTerm>() {

    override fun match(actual: FieldReadTerm) {
        subject.match(actual = actual.subject)
        fieldName.match(actual = actual.fieldName)
    }
}
