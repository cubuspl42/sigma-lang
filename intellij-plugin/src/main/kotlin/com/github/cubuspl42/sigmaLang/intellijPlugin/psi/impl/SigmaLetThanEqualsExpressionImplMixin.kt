package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaLessThanEqualsExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

abstract class SigmaLetThanEqualsExpressionImplMixin(
    node: ASTNode,
) : SigmaUnimplementedExpressionImplMixin(node), SigmaLessThanEqualsExpression
