package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaReferenceExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceTerm

abstract class SigmaReferenceExpressionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaReferenceExpression {
    final override val asTerm: ReferenceTerm = object : PsiExpressionTerm(), ReferenceTerm {
        override val referredName: Identifier
            get() = Identifier.of(
                name = this@SigmaReferenceExpressionImplMixin.referredNameElement.text,
            )
    }
}
