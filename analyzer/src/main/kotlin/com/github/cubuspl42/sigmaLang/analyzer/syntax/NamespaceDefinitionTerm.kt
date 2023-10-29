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

val NamespaceDefinitionTerm.segregatedEntries: NamespaceDefinitionTerm.SegregatedEntries
    get() {
        val primDefinitionTerms = mutableListOf<DefinitionTerm>()
        val metaDefinitionTerms = mutableListOf<MetaDefinitionTerm>()

        for (entry in entries) {
            when (entry) {
                is MetaDefinitionTerm -> metaDefinitionTerms.add(entry)
                is DefinitionTerm -> primDefinitionTerms.add(entry)
                else -> throw IllegalStateException("Unknown namespace entry type: ${entry::class.simpleName}")
            }
        }

        return NamespaceDefinitionTerm.SegregatedEntries(
            primDefinitionTerms = primDefinitionTerms,
            metaDefinitionTerms = metaDefinitionTerms,
        )
    }
