package com.github.cubuspl42.sigmaLang.intellijPlugin.psi

import com.intellij.psi.PsiElement
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntryTerm

interface SigmaNamespaceEntry : PsiElement {
    val asTerm: NamespaceEntryTerm
}
