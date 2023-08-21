package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionSourceTerm

abstract class SigmaBinaryExpressionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaExpression {
    final override fun toTerm(): AbstractionSourceTerm = TODO()
}
