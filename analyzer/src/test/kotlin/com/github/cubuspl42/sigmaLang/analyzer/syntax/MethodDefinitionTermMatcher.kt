package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import utils.Matcher

class MethodDefinitionTermMatcher(
    private val self: Matcher<ExpressionTerm>,
    private val name: Matcher<Identifier>,
    private val body: Matcher<AbstractionConstructorTerm>,
) : Matcher<MethodDefinitionTerm>() {
    override fun match(actual: MethodDefinitionTerm) {
        self.match(actual.instanceType)
        name.match(actual.name)
        body.match(actual.body)
    }
}
