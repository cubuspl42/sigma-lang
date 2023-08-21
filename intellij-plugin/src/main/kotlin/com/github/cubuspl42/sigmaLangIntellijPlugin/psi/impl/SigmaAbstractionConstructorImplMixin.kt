package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaAbstractionConstructor
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.ext.getSourceLocation
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import sigma.syntax.expressions.AbstractionSourceTerm
import sigma.syntax.expressions.TupleTypeConstructorSourceTerm

abstract class SigmaAbstractionConstructorImplMixin(
    node: ASTNode,
) : ASTWrapperPsiElement(node), SigmaAbstractionConstructor {
    final override fun toTerm(): AbstractionSourceTerm = AbstractionSourceTerm(
        location = getSourceLocation(),
        genericParametersTuple = null,
        argumentType = argumentType.toTerm() as TupleTypeConstructorSourceTerm,
        declaredImageType = null,
        image = image!!.toTerm(),
    )
}
