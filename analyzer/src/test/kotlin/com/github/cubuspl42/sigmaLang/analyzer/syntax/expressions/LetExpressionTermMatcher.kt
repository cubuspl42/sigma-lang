package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import utils.Matcher

import com.github.cubuspl42.sigmaLang.analyzer.syntax.LocalDefinitionTerm

class LetExpressionTermMatcher(
    val definitions: Matcher<List<LocalDefinitionTerm>>,
    val result: Matcher<ExpressionTerm>,
) : Matcher<LetExpressionTerm>() {
    override fun match(actual: LetExpressionTerm) {
        definitions.match(actual = actual.definitions)
        result.match(actual = actual.result)
    }
}
