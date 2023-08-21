package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaLetExpression
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.ext.getSourceLocation
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import sigma.syntax.expressions.ExpressionSourceTerm
import sigma.syntax.expressions.LetExpressionSourceTerm
import sigma.syntax.expressions.LocalScopeSourceTerm

abstract class SigmaLetExpressionImplMixin(node: ASTNode) : ASTWrapperPsiElement(node), SigmaLetExpression {
    fun getNames(): Set<String> = setOf("local1", "local2")

    final override fun toTerm(): ExpressionSourceTerm = LetExpressionSourceTerm(
        location = getSourceLocation(),
        localScope = LocalScopeSourceTerm(
            location = getSourceLocation(),
            definitions = letExpressionScopeEntryList.map { it.toTerm() },
        ),
        result = result!!.toTerm(),
    )
}
