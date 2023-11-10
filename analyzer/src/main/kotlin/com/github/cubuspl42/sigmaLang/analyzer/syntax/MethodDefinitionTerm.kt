package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.TupleTypeConstructorTerm

interface MethodDefinitionTerm : NamespaceEntryTerm {
    val metaArgumentType: TupleTypeConstructorTerm?

    val thisType: ExpressionTerm

    val body: ExpressionTerm
}
