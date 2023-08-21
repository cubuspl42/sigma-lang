package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaConstantDefinition
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.ext.getSourceLocation
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import sigma.evaluation.values.Symbol
import sigma.syntax.ConstantDefinitionSourceTerm

abstract class SigmaConstantDefinitionImplMixin(node: ASTNode) : ASTWrapperPsiElement(node), SigmaConstantDefinition {
    final override fun toTerm(): ConstantDefinitionSourceTerm = ConstantDefinitionSourceTerm(
        location = getSourceLocation(),
        name = Symbol.of(definedName.text),
        declaredTypeBody = null,
        body = body.toTerm(),
    )
}
