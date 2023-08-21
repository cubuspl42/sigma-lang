package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaCallExpression
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaIntLiteral
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.ext.getSourceLocation
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import sigma.evaluation.values.IntValue
import sigma.syntax.expressions.AbstractionTerm
import sigma.syntax.expressions.IntLiteralTerm

abstract class SigmaIntLiteralImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaIntLiteral {
    final override fun toTerm(): IntLiteralTerm = IntLiteralTerm(
        location = getSourceLocation(),
        value = IntValue(
            value = content.text.toLong(),
        ),
    )
}
