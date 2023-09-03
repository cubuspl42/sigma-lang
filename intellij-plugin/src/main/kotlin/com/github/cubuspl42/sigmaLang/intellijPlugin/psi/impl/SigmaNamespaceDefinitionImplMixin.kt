package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaNamespaceDefinition
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaNamespaceEntry
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.ext.descendantsOfType
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntryTerm

abstract class SigmaNamespaceDefinitionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaNamespaceDefinition {
    final override val asTerm: NamespaceEntryTerm
        get() = TODO()

    private fun getNamespaceEntries(): Collection<SigmaNamespaceEntry> = descendantsOfType<SigmaNamespaceEntry>()
}
