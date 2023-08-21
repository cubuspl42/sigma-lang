package com.github.cubuspl42.sigmaLangIntellijPlugin.psi

import com.intellij.psi.PsiElement
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntrySourceTerm

interface SigmaNamespaceEntry : PsiElement {
    fun toTerm(): NamespaceEntrySourceTerm
}
