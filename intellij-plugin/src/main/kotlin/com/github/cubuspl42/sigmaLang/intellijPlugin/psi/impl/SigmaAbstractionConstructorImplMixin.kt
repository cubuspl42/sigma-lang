package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.GenericParametersTuple
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.TupleTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaAbstractionConstructor
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class SigmaAbstractionConstructorImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaAbstractionConstructor {
    override val asTerm: ExpressionTerm = object : PsiExpressionTerm(), AbstractionTerm {
        override val genericParametersTuple: GenericParametersTuple?
            get() = null
        override val argumentType: TupleTypeConstructorTerm
            get() = this@SigmaAbstractionConstructorImplMixin.argumentTypeElement.asTerm as TupleTypeConstructorTerm
        override val declaredImageType: ExpressionTerm?
            get() = null
        override val image: ExpressionTerm
            get() = this@SigmaAbstractionConstructorImplMixin.imageElement!!.asTerm
    }
}
