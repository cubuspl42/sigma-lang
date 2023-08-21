package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaNamespaceDefinition
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaNamespaceEntry
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.ext.descendantsOfType
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.ext.getSourceLocation
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntrySourceTerm

abstract class SigmaNamespaceDefinitionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaNamespaceDefinition {
    final override fun toTerm(): NamespaceDefinitionSourceTerm = NamespaceDefinitionSourceTerm(
        location = getSourceLocation(),
        name = Symbol.of(definedName.text),
        namespaceEntries = getNamespaceEntries().map { it as NamespaceEntrySourceTerm },
    )

    private fun getNamespaceEntries(): Collection<SigmaNamespaceEntry> = descendantsOfType<SigmaNamespaceEntry>()
}
