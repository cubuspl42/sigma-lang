package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ParenTerm
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaParenExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class SigmaParenExpressionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaParenExpression {
    final override val asTerm: ParenTerm = object : PsiExpressionTerm(), ParenTerm {
        override val wrappedTerm: ExpressionTerm
            get() = this@SigmaParenExpressionImplMixin.wrappedExpression!!.asTerm
    }
}
