package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaCallExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionSourceTerm

abstract class SigmaCallExpressionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaCallExpression {
    final override fun toTerm(): AbstractionSourceTerm = TODO()
}
