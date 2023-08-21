package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionSourceTerm

abstract class SigmaGenericExpressionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaExpression {
    final override fun toTerm(): AbstractionSourceTerm = TODO()
}
