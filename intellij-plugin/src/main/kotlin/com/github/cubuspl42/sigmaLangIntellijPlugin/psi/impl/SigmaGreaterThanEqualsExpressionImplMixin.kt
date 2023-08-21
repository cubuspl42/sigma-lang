package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaGreaterThanEqualsExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import sigma.syntax.expressions.AbstractionSourceTerm

abstract class SigmaGreaterThanEqualsExpressionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaGreaterThanEqualsExpression {
    final override fun toTerm(): AbstractionSourceTerm = TODO()
}
