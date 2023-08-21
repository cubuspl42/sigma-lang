package sigma.syntax

import sigma.evaluation.values.Symbol

interface NamespaceDefinitionTerm : NamespaceEntryTerm {
    val name: Symbol

    val namespaceEntries: List<NamespaceEntrySourceTerm>
}
