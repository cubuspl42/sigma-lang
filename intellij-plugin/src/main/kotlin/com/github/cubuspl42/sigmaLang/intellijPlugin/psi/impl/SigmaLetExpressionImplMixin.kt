package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaLetExpression
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.ext.getSourceLocation
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.LetExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.LocalScopeSourceTerm

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
