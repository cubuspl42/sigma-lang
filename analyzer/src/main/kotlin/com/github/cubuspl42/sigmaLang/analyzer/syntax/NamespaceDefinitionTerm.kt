package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol

interface NamespaceDefinitionTerm : NamespaceEntryTerm {
    override val name: Symbol

    val namespaceEntries: List<NamespaceEntryTerm>
}
