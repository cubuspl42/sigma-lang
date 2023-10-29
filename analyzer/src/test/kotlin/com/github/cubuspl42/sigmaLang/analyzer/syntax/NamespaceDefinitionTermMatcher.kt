package com.github.cubuspl42.sigmaLang.analyzer.syntax

import utils.Matcher
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier

class NamespaceDefinitionTermMatcher(
    val name: Matcher<Identifier>,
    val namespaceEntries: Matcher<List<NamespaceEntryTerm>>,
) : Matcher<NamespaceDefinitionTerm>() {
    override fun match(actual: NamespaceDefinitionTerm) {
        name.match(actual = actual.name)
        namespaceEntries.match(actual = actual.entries)
    }
}
