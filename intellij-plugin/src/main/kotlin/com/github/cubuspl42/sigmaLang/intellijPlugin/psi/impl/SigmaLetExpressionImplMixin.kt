package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.analyzer.syntax.LocalDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.LetExpressionTerm
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaLetExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class SigmaLetExpressionImplMixin(node: ASTNode) : ASTWrapperPsiElement(node), SigmaLetExpression {
    fun getNames(): Set<String> = setOf("local1", "local2")

    final override val asTerm: LetExpressionTerm = object : PsiExpressionTerm(), LetExpressionTerm {
        override val definitions: List<LocalDefinitionTerm>
            get() = this@SigmaLetExpressionImplMixin.letExpressionScopeEntryList.map { it.asTerm }

        override val result: ExpressionTerm
            get() = this@SigmaLetExpressionImplMixin.resultElement!!.asTerm
    }
}
