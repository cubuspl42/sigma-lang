package com.github.cubuspl42.sigmaLangIntellijPlugin.psi

import com.intellij.psi.PsiElement
import sigma.syntax.LocalDefinitionTerm
import sigma.syntax.NamespaceEntryTerm
import sigma.syntax.expressions.ExpressionTerm

interface SigmaLetExpressionScopeEntryBase : PsiElement {
    fun toTerm(): LocalDefinitionTerm
}
