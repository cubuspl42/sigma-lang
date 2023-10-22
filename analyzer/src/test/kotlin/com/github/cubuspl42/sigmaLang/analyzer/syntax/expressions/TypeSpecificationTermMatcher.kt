package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import utils.Matcher

class TypeSpecificationTermMatcher(
    val subject: Matcher<ExpressionTerm>,
    val argument: Matcher<TupleConstructorTerm>,
) : Matcher<TypeSpecificationTerm>() {
    override fun match(actual: TypeSpecificationTerm) {
        subject.match(actual = actual.subject)
        argument.match(actual = actual.argument)
    }
}
