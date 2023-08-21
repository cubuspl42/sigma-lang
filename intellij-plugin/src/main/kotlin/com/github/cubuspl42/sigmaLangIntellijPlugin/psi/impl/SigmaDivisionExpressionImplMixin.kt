package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaDivisionExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionSourceTerm

abstract class SigmaDivisionExpressionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaDivisionExpression {
    final override fun toTerm(): AbstractionSourceTerm = TODO()
}
