package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class SigmaLetExpressionImplMixin(node: ASTNode) : ASTWrapperPsiElement(node) {
    fun getNames(): Set<String> = setOf("local1", "local2")
}
