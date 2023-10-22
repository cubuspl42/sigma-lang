package com.github.cubuspl42.sigmaLang.analyzer.syntax

import utils.Matcher
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

class DefinitionTermMatcher(
    val name: Matcher<Identifier>,
    val declaredTypeBody: Matcher<ExpressionTerm?>,
    val body: Matcher<ExpressionTerm>
) : Matcher<DefinitionTerm>() {
    override fun match(actual: DefinitionTerm) {
        name.match(actual = actual.name)
        declaredTypeBody.match(actual = actual.declaredTypeBody)
        body.match(actual = actual.body)
    }
}
