package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.PostfixCallTerm
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaExpression
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaPostfixCallExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class SigmaCallExpressionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaPostfixCallExpression {
    override val asTerm: ExpressionTerm = object : PsiExpressionTerm(), PostfixCallTerm {
        override val subject: ExpressionTerm
            get() = this@SigmaCallExpressionImplMixin.subject.asTerm

        override val argument: ExpressionTerm
            get() = (this@SigmaCallExpressionImplMixin.children[1] as SigmaExpression).asTerm
    }
}
