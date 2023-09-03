package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaConstantDefinition
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ConstantDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

abstract class SigmaConstantDefinitionImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaConstantDefinition {
    final override val asTerm: ConstantDefinitionTerm = object : ConstantDefinitionTerm {
        override val name: Symbol
            get() = Symbol.of(definedNameElement.text)

        override val declaredTypeBody: ExpressionTerm?
            get() = null

        override val body: ExpressionTerm
            get() = bodyElement.asTerm
    }
}
