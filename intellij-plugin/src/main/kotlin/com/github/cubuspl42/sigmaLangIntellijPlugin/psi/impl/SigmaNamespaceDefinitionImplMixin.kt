package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaNamespaceDefinition
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaNamespaceEntry
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.ext.descendantsOfType
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.ext.getSourceLocation
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import sigma.evaluation.values.Symbol
import sigma.syntax.NamespaceDefinitionTerm
import sigma.syntax.NamespaceEntryTerm

abstract class SigmaNamespaceDefinitionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaNamespaceDefinition {
    final override fun toTerm(): NamespaceDefinitionTerm = NamespaceDefinitionTerm(
        location = getSourceLocation(),
        name = Symbol.of(definedName.text),
        namespaceEntries = getNamespaceEntries().map { it as NamespaceEntryTerm },
    )

    private fun getNamespaceEntries(): Collection<SigmaNamespaceEntry> = descendantsOfType<SigmaNamespaceEntry>()
}
