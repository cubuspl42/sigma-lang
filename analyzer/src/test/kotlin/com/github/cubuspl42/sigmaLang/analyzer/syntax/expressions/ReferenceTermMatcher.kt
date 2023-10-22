package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import utils.Matcher

class ReferenceTermMatcher(
    private val referredName: Matcher<Identifier>,
) : Matcher<ReferenceTerm>() {
    override fun match(actual: ReferenceTerm) {
        referredName.match(actual = actual.referredName)
    }
}
