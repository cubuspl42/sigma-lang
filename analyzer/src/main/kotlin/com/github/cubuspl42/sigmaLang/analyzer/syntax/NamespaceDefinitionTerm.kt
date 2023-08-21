package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol

interface NamespaceDefinitionTerm : NamespaceEntryTerm {
    val name: Symbol

    val namespaceEntries: List<NamespaceEntrySourceTerm>
}
