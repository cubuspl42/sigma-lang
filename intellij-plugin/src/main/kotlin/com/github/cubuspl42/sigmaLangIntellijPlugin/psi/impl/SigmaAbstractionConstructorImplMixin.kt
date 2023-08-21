package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaAbstractionConstructor
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.ext.getSourceLocation
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import sigma.syntax.expressions.AbstractionTerm
import sigma.syntax.expressions.TupleTypeConstructorTerm

abstract class SigmaAbstractionConstructorImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaAbstractionConstructor {
    final override fun toTerm(): AbstractionTerm = AbstractionTerm(
        location = getSourceLocation(),
        genericParametersTuple = null,
        argumentType = argumentType.toTerm() as TupleTypeConstructorTerm,
        declaredImageType = null,
        image = image!!.toTerm(),
    )
}
