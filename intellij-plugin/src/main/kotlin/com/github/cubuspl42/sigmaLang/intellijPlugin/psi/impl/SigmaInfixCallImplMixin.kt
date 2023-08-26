package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.InfixCallTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.InfixOperator
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaExpression
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class SigmaInfixCallImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaExpression {
    override val asTerm: ExpressionTerm = object : PsiExpressionTerm(), InfixCallTerm {
        override val operator: InfixOperator
            get() {
                val operatorElement = this@SigmaInfixCallImplMixin.children[1]
                return InfixOperator.fromSymbol(symbol = operatorElement.text)
            }

        override val leftArgument: ExpressionTerm
            get() = (this@SigmaInfixCallImplMixin.children[0] as SigmaExpression).asTerm

        override val rightArgument: ExpressionTerm
            get() = (this@SigmaInfixCallImplMixin.children[2] as SigmaExpression).asTerm
    }
}
