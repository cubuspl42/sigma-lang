package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

interface MethodDefinitionTerm : NamespaceEntryTerm {
    val thisType: ExpressionTerm

    val body: AbstractionConstructorTerm
}
