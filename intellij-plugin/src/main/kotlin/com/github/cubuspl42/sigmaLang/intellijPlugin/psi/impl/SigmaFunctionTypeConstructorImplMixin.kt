package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.FunctionTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.TupleTypeConstructorTerm
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaFunctionTypeConstructor
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class SigmaFunctionTypeConstructorImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaFunctionTypeConstructor {
    override val asTerm: FunctionTypeConstructorTerm = object : PsiExpressionTerm(), FunctionTypeConstructorTerm {
        override val metaArgumentType: TupleTypeConstructorTerm?
            get() = this@SigmaFunctionTypeConstructorImplMixin.genericParametersTuple?.tupleTypeConstructor?.asTerm

        override val argumentType: TupleTypeConstructorTerm
            get() = this@SigmaFunctionTypeConstructorImplMixin.argumentType.asTerm

        override val imageType: ExpressionTerm
            get() = this@SigmaFunctionTypeConstructorImplMixin.imageType.expression.asTerm
    }
}
