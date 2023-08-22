package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaGreaterThanExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

abstract class SigmaGreaterThanExpressionImplMixin(
    node: ASTNode,
) : SigmaUnimplementedExpressionImplMixin(node), SigmaGreaterThanExpression
