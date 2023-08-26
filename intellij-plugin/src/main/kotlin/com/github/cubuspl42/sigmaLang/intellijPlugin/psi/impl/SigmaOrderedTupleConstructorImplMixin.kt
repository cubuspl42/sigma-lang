package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaOrderedTupleConstructor
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaOrderedTupleTypeConstructor
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class SigmaOrderedTupleConstructorImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaOrderedTupleConstructor {
    override val asTerm: OrderedTupleConstructorTerm = object : PsiExpressionTerm(), OrderedTupleConstructorTerm {
        override val elements: List<ExpressionTerm>
            get() = this@SigmaOrderedTupleConstructorImplMixin.expressionList.map { it.asTerm }
    }
}
