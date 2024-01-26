package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

sealed interface DefinitionTerm {
    val name: Identifier
    val declaredTypeBody: ExpressionTerm?
    val body: ExpressionTerm
}
