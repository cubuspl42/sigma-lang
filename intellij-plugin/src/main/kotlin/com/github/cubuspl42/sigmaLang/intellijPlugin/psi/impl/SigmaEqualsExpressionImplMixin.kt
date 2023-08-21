package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaEqualsExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionSourceTerm

abstract class SigmaEqualsExpressionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaEqualsExpression {
    final override fun toTerm(): AbstractionSourceTerm = TODO()
}
