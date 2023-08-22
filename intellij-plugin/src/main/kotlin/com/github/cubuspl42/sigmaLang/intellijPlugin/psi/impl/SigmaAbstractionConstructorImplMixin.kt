package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaAbstractionConstructor
import com.intellij.lang.ASTNode

abstract class SigmaAbstractionConstructorImplMixin(
    node: ASTNode,
) : SigmaUnimplementedExpressionImplMixin(node), SigmaAbstractionConstructor
