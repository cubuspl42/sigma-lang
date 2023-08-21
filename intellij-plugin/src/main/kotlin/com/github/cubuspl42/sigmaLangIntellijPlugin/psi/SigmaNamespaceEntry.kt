package com.github.cubuspl42.sigmaLangIntellijPlugin.psi

import com.intellij.psi.PsiElement
import sigma.syntax.NamespaceEntrySourceTerm

interface SigmaNamespaceEntry : PsiElement {
    fun toTerm(): NamespaceEntrySourceTerm
}
