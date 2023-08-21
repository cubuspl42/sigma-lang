package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaIfExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionSourceTerm

abstract class SigmaIfExpressionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaIfExpression {
    final override fun toTerm(): AbstractionSourceTerm = TODO()
}
