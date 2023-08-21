package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaLetExpressionScopeEntry
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.ext.getSourceLocation
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import sigma.evaluation.values.Symbol
import sigma.syntax.LocalDefinitionTerm

abstract class SigmaLetExpressionScopeEntryImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaLetExpressionScopeEntry {
    final override fun toTerm(): LocalDefinitionTerm = LocalDefinitionTerm(
        location = getSourceLocation(),
        name = Symbol.of(definedName.text),
        declaredTypeBody = null,
        body = body.toTerm(),
    )
}
