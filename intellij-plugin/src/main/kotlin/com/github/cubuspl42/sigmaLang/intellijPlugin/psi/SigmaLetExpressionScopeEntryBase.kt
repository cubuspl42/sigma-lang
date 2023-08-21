package com.github.cubuspl42.sigmaLang.intellijPlugin.psi

import com.intellij.psi.PsiElement
import com.github.cubuspl42.sigmaLang.analyzer.syntax.LocalDefinitionSourceTerm

interface SigmaLetExpressionScopeEntryBase : PsiElement {
    fun toTerm(): LocalDefinitionSourceTerm
}
