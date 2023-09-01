package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ArrayTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaArrayTypeConstructor
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaOrderedTupleTypeConstructor
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.util.elementType

abstract class SigmaArrayTypeConstructorImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaArrayTypeConstructor {
    override val asTerm: ArrayTypeConstructorTerm = object : PsiExpressionTerm(), ArrayTypeConstructorTerm {
        override val elementType: ExpressionTerm
            get() = this@SigmaArrayTypeConstructorImplMixin.elementType.expression.asTerm
    }
}
