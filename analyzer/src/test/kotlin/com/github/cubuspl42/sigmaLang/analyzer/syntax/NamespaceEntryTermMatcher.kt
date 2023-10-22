package com.github.cubuspl42.sigmaLang.analyzer.syntax

import utils.Matcher
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier

class NamespaceEntryTermMatcher(
    val name: Matcher<Identifier>,
) : Matcher<NamespaceEntryTerm>() {

    override fun match(actual: NamespaceEntryTerm) {
        name.match(actual = actual.name)
    }
}
