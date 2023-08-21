package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaLetExpression
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.ext.getSourceLocation
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.LetExpressionTerm
import sigma.syntax.expressions.LocalScopeTerm

abstract class SigmaLetExpressionImplMixin(node: ASTNode) : ASTWrapperPsiElement(node), SigmaLetExpression {
    fun getNames(): Set<String> = setOf("local1", "local2")

    final override fun toTerm(): ExpressionTerm = LetExpressionTerm(
        location = getSourceLocation(),
        localScope = LocalScopeTerm(
            location = getSourceLocation(),
            definitions = letExpressionScopeEntryList.map { it.toTerm() },
        ),
        result = result!!.toTerm(),
    )
}
