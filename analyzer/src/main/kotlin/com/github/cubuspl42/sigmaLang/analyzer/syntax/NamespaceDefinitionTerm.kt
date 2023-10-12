package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier

interface NamespaceDefinitionTerm : NamespaceEntryTerm {
    override val name: Identifier

    val namespaceEntries: List<NamespaceEntryTerm>
}
