package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaReferenceExpression
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.ext.getSourceLocation
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceSourceTerm

abstract class SigmaReferenceExpressionImplMixin(node: ASTNode) : ASTWrapperPsiElement(node), SigmaReferenceExpression {
   final override fun toTerm(): ExpressionSourceTerm = ReferenceSourceTerm(
       location = getSourceLocation(),
       referredName = Symbol.of(referredName.text)
   )
}
