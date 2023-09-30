package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.TupleTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaAbstractionConstructor
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaTupleTypeConstructor
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class SigmaAbstractionConstructorImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaAbstractionConstructor {
    override val asTerm: ExpressionTerm = object : PsiExpressionTerm(), AbstractionConstructorTerm {
        override val metaArgumentType: TupleTypeConstructorTerm?
            get() = null

        override val argumentType: TupleTypeConstructorTerm
            get() = (this@SigmaAbstractionConstructorImplMixin.argumentType as SigmaTupleTypeConstructor).asTerm

        override val declaredImageType: ExpressionTerm?
            get() = this@SigmaAbstractionConstructorImplMixin.image?.asTerm

        override val image: ExpressionTerm
            get() = this@SigmaAbstractionConstructorImplMixin.image!!.asTerm
    }
}
