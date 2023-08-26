package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IsUndefinedCheckTerm
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaIsUndefinedExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class SigmaIsUndefinedExpressionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaIsUndefinedExpression {
    final override val asTerm: IsUndefinedCheckTerm = object : PsiExpressionTerm(), IsUndefinedCheckTerm {
        override val argument: ExpressionTerm
            get() = this@SigmaIsUndefinedExpressionImplMixin.expression!!.asTerm
    }
}
