package com.github.cubuspl42.sigmaLangIntellijPlugin.psi

import com.intellij.psi.PsiElement
import sigma.syntax.NamespaceEntryTerm
import sigma.syntax.expressions.ExpressionTerm

interface SigmaExpressionBase : PsiElement {
    fun toTerm(): ExpressionTerm = TODO()
}
