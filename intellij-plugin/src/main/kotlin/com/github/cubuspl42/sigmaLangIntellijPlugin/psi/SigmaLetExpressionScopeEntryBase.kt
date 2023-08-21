package com.github.cubuspl42.sigmaLangIntellijPlugin.psi

import com.intellij.psi.PsiElement
import sigma.syntax.LocalDefinitionSourceTerm

interface SigmaLetExpressionScopeEntryBase : PsiElement {
    fun toTerm(): LocalDefinitionSourceTerm
}
