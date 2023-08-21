package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaReferenceExpression
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.ext.getSourceLocation
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import sigma.evaluation.values.Symbol
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.ReferenceTerm

abstract class SigmaReferenceExpressionImplMixin(node: ASTNode) : ASTWrapperPsiElement(node), SigmaReferenceExpression {
   final override fun toTerm(): ExpressionTerm = ReferenceTerm(
       location = getSourceLocation(),
       referee = Symbol.of(referredName.text)
   )
}
