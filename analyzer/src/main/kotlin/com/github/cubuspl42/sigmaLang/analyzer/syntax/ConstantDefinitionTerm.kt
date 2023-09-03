package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

interface ConstantDefinitionTerm : NamespaceEntryTerm {
    val name: Symbol

    val declaredTypeBody: ExpressionTerm?

    val body: ExpressionTerm
}
