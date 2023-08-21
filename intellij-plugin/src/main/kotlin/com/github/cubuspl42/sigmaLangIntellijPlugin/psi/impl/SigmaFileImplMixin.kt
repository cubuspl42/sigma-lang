package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaNamespaceEntry
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaTypes
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.ext.descendantsOfType
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import sigma.syntax.ModuleTerm
import sigma.syntax.NamespaceEntryTerm
import sigma.syntax.SourceLocation
import sigma.syntax.Term

abstract class SigmaFileImplMixin(node: ASTNode) : ASTWrapperPsiElement(node) {
    fun toTerm(): ModuleTerm = ModuleTerm(
        location = SourceLocation(0, 0),
        imports = emptyList(),
        namespaceEntries = getNamespaceEntries().map { it as NamespaceEntryTerm }
    )

    private fun getNamespaceEntries(): Collection<SigmaNamespaceEntry> = descendantsOfType<SigmaNamespaceEntry>()
}


