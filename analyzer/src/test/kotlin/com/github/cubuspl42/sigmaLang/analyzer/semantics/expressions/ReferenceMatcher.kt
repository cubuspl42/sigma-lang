package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration
import utils.Matcher

class ReferenceMatcher(
    private val referredDeclaration: Matcher<Declaration>,
) : Matcher<Reference>() {
    override fun match(actual: Reference) {
        referredDeclaration.match(actual = actual.referredDeclaration)
    }
}
