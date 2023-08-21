package com.github.cubuspl42.sigmaLangIntellijPlugin.psi

import com.intellij.psi.PsiElement
import sigma.syntax.NamespaceEntryTerm

interface SigmaNamespaceEntry : PsiElement {
    fun toTerm(): NamespaceEntryTerm
}
