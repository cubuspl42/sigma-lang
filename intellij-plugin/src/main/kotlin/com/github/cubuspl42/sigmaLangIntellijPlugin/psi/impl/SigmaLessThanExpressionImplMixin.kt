package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaLessThanExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionSourceTerm

abstract class SigmaLessThanExpressionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaLessThanExpression {
    final override fun toTerm(): AbstractionSourceTerm = TODO()
}
