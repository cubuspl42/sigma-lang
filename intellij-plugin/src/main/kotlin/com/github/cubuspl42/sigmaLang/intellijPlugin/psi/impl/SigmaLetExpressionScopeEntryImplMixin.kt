package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaLetExpressionScopeEntry
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.LocalDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

abstract class SigmaLetExpressionScopeEntryImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaLetExpressionScopeEntry {
    final override val asTerm: LocalDefinitionTerm = object : LocalDefinitionTerm {
        override val name: Symbol
            get() = Symbol.of(this@SigmaLetExpressionScopeEntryImplMixin.definedNameElement.text)

        override val declaredTypeBody: ExpressionTerm?
            get() = null

        override val body: ExpressionTerm
            get() = this@SigmaLetExpressionScopeEntryImplMixin.bodyElement.asTerm
    }
}
