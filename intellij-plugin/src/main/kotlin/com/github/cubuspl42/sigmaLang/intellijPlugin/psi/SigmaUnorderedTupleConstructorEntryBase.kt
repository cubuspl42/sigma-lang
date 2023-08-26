package com.github.cubuspl42.sigmaLang.intellijPlugin.psi

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorTerm
import com.intellij.psi.PsiElement

interface SigmaUnorderedTupleConstructorEntryBase : PsiElement {
    val asEntry: UnorderedTupleConstructorTerm.Entry
}
