package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaGreaterThanExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import sigma.syntax.expressions.AbstractionSourceTerm

abstract class SigmaGreaterThanExpressionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaGreaterThanExpression {
    final override fun toTerm(): AbstractionSourceTerm = TODO()
}
