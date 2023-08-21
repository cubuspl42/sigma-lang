package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaLessThanEqualsExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import sigma.syntax.expressions.AbstractionSourceTerm

abstract class SigmaLetThanEqualsExpressionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaLessThanEqualsExpression {
    final override fun toTerm(): AbstractionSourceTerm = TODO()
}
