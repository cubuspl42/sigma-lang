package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaCallExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import sigma.syntax.expressions.AbstractionSourceTerm

abstract class SigmaCallExpressionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaCallExpression {
    final override fun toTerm(): AbstractionSourceTerm = TODO()
}
