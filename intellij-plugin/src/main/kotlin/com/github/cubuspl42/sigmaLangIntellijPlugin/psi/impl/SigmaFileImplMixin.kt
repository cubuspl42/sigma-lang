package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaNamespaceEntry
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.ext.descendantsOfType
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ModuleSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntrySourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

abstract class SigmaFileImplMixin(node: ASTNode) : ASTWrapperPsiElement(node) {
    fun toTerm(): ModuleSourceTerm = ModuleSourceTerm(
        location = SourceLocation(0, 0),
        imports = emptyList(),
        namespaceEntries = getNamespaceEntries().map { it as NamespaceEntrySourceTerm }
    )

    private fun getNamespaceEntries(): Collection<SigmaNamespaceEntry> = descendantsOfType<SigmaNamespaceEntry>()
}


