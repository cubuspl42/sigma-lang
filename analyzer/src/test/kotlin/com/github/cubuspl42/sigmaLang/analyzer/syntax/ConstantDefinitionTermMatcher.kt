package com.github.cubuspl42.sigmaLang.analyzer.syntax

import utils.Matcher
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

class ConstantDefinitionTermMatcher(
    val name: Matcher<Identifier>,
    val declaredTypeBody: Matcher<ExpressionTerm?>,
    val body: Matcher<ExpressionTerm>
) : Matcher<ConstantDefinitionTerm>() {
    override fun match(actual: ConstantDefinitionTerm) {
        name.match(actual = actual.name)
        declaredTypeBody.match(actual = actual.declaredTypeBody)
        body.match(actual = actual.body)
    }
}
