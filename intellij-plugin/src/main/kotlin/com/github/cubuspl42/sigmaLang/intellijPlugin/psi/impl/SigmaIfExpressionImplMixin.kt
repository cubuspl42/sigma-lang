package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IfExpressionTerm
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaIfExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class SigmaIfExpressionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaIfExpression {
    override val asTerm: ExpressionTerm = object : PsiExpressionTerm(), IfExpressionTerm {
        override val guard: ExpressionTerm
            get() = this@SigmaIfExpressionImplMixin.guard!!.asTerm

        override val trueBranch: ExpressionTerm
            get() = this@SigmaIfExpressionImplMixin.ifExpressionBody!!.trueBranch.asTerm

        override val falseBranch: ExpressionTerm
            get() = this@SigmaIfExpressionImplMixin.ifExpressionBody!!.falseBranch!!.asTerm
    }
}
