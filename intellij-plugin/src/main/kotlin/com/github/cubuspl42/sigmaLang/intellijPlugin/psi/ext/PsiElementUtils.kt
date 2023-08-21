package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.ext

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

fun PsiElement.getSourceLocation(): SourceLocation {
    // TODO: Use DocumentUtil
    return SourceLocation(
        lineIndex = 1,
        columnIndex = 0,
    )
}

inline fun <reified T : PsiElement> PsiElement.descendantsOfType(): Collection<T> =
    PsiTreeUtil.findChildrenOfType(this, T::class.java)
