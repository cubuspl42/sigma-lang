package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaIntLiteral
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.ext.getSourceLocation
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ConstantDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IntLiteralSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IntLiteralTerm

abstract class SigmaIntLiteralImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaIntLiteral {
    final override val asTerm: IntLiteralTerm = object : IntLiteralTerm {
        override val value: IntValue
            get() = IntValue(
                value = this@SigmaIntLiteralImplMixin.valueElement.text.toLong(),
            )
    }
}
