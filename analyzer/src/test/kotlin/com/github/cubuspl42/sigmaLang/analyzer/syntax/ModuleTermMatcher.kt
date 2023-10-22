
package com.github.cubuspl42.sigmaLang.analyzer.syntax

import utils.Matcher

class ModuleTermMatcher(
    val imports: Matcher<List<ImportTerm>>,
    val namespaceEntries: Matcher<List<NamespaceEntryTerm>>
) : Matcher<ModuleTerm>() {

    override fun match(actual: ModuleTerm) {
        imports.match(actual = actual.imports)
        namespaceEntries.match(actual = actual.namespaceEntries)
    }
}
