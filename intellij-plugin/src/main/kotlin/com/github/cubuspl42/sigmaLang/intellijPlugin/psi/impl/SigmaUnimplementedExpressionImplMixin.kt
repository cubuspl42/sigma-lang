package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

abstract class SigmaUnimplementedExpressionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaExpression {
    final override val asTerm: ExpressionTerm
        get() = TODO()
}
