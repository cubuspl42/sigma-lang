package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaLetExpressionScopeEntry
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.ext.getSourceLocation
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.LocalDefinitionSourceTerm

abstract class SigmaLetExpressionScopeEntryImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaLetExpressionScopeEntry {
    final override fun toTerm(): LocalDefinitionSourceTerm = LocalDefinitionSourceTerm(
        location = getSourceLocation(),
        name = Symbol.of(definedName.text),
        declaredTypeBody = null,
        body = body.toTerm(),
    )
}
