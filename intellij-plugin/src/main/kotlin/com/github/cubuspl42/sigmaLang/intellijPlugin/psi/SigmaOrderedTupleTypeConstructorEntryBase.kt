package com.github.cubuspl42.sigmaLang.intellijPlugin.psi

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleTypeConstructorTerm
import com.intellij.psi.PsiElement

interface SigmaOrderedTupleTypeConstructorEntryBase : PsiElement {
    val asElement: OrderedTupleTypeConstructorTerm.Element
}
