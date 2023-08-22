package com.github.cubuspl42.sigmaLang.intellijPlugin.psi

import com.intellij.psi.PsiElement
import com.github.cubuspl42.sigmaLang.analyzer.syntax.LocalDefinitionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.LocalDefinitionTerm

interface SigmaLetExpressionScopeEntryBase : PsiElement {
    val asTerm: LocalDefinitionTerm
}
