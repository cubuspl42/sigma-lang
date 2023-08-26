package com.github.cubuspl42.sigmaLang.intellijPlugin.psi

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleTypeConstructorTerm
import com.intellij.psi.PsiElement

interface SigmaUnorderedTupleTypeConstructorEntryBase : PsiElement {
    val asEntry: UnorderedTupleTypeConstructorTerm.Entry
}
