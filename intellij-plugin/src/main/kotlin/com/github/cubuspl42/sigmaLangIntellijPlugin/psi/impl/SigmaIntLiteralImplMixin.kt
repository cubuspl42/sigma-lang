package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaIntLiteral
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.ext.getSourceLocation
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IntLiteralSourceTerm

abstract class SigmaIntLiteralImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaIntLiteral {
    final override fun toTerm(): IntLiteralSourceTerm = IntLiteralSourceTerm(
        location = getSourceLocation(),
        value = IntValue(
            value = content.text.toLong(),
        ),
    )
}
