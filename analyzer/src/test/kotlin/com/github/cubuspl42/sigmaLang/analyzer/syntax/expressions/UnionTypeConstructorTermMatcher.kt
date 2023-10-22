package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import utils.Matcher

class UnionTypeConstructorTermMatcher(
    val leftType: Matcher<ExpressionTerm>,
    val rightType: Matcher<ExpressionTerm>,
) : Matcher<UnionTypeConstructorTerm>() {
    override fun match(actual: UnionTypeConstructorTerm) {
        leftType.match(actual = actual.leftType)
        rightType.match(actual = actual.rightType)
    }
}
