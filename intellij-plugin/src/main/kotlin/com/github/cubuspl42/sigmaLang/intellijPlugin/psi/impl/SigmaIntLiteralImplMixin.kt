package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaIntLiteral
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.*

abstract class SigmaIntLiteralImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaIntLiteral {
    final override val asTerm: IntLiteralTerm = object : PsiExpressionTerm(), IntLiteralTerm {
        override val value: IntValue
            get() = IntValue(
                value = this@SigmaIntLiteralImplMixin.valueElement.text.toLong(),
            )
    }
}
