package com.github.cubuspl42.sigmaLang.analyzer.syntax

interface ModuleTerm {
    val imports: List<ImportTerm>
    val namespaceEntries: List<NamespaceEntryTerm>
}
