package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceTerm

interface MethodDefinitionTerm : NamespaceEntryTerm {
    val instanceType: ReferenceTerm

    val body: AbstractionConstructorTerm
}
