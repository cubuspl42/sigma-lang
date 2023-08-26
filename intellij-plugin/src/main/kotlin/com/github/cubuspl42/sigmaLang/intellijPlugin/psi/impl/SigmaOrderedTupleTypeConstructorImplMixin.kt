package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaOrderedTupleTypeConstructor
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class SigmaOrderedTupleTypeConstructorImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaOrderedTupleTypeConstructor {
    override val asTerm: OrderedTupleTypeConstructorTerm = object : PsiExpressionTerm(), OrderedTupleTypeConstructorTerm {
        override val elements: List<OrderedTupleTypeConstructorTerm.Element>
            get() = orderedTupleTypeConstructorEntryList.map { it.asElement }
    }
}
