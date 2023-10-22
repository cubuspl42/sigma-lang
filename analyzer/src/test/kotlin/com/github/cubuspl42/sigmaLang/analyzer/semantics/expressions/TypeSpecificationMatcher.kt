package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import utils.Matcher

class TypeSpecificationMatcher(
    private val subject: Matcher<Expression>,
    private val metaArgument: Matcher<DictValue>,
) : Matcher<TypeSpecification>() {
    override fun match(actual: TypeSpecification) {
        subject.match(actual = actual.subject)
        metaArgument.match(actual = actual.metaArgument)
    }
}
