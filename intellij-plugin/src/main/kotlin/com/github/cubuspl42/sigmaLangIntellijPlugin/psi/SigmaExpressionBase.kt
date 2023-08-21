package com.github.cubuspl42.sigmaLangIntellijPlugin.psi

import com.intellij.psi.PsiElement
import sigma.syntax.expressions.ExpressionSourceTerm

interface SigmaExpressionBase : PsiElement {
    fun toTerm(): ExpressionSourceTerm = TODO()
}
