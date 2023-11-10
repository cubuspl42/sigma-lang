package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.TupleTypeConstructorTerm
import utils.Matcher

class MethodDefinitionTermMatcher(
    private val metaArgumentType: Matcher<TupleTypeConstructorTerm?> = Matcher.IsNull(),
    private val thisType: Matcher<ExpressionTerm>,
    private val name: Matcher<Identifier>,
    private val body: Matcher<ExpressionTerm>,
) : Matcher<MethodDefinitionTerm>() {
    override fun match(actual: MethodDefinitionTerm) {
        metaArgumentType.match(actual.metaArgumentType)
        thisType.match(actual.thisType)
        name.match(actual.name)
        body.match(actual.body)
    }
}
