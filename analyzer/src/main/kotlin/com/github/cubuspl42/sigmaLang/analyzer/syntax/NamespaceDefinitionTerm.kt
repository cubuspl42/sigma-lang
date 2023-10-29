package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier

interface NamespaceDefinitionTerm : NamespaceEntryTerm {
    data class SegregatedEntries(
        val primDefinitionTerms: List<DefinitionTerm>,
        val metaDefinitionTerms: List<MetaDefinitionTerm>,
    )

    override val name: Identifier

    val entries: List<NamespaceEntryTerm>
}
