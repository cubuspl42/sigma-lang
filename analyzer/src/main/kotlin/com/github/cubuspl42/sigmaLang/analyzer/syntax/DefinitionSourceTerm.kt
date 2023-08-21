package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm

sealed interface DefinitionSourceTerm : DefinitionTerm {
    override val declaredTypeBody: ExpressionSourceTerm?
    override val body: ExpressionSourceTerm
}
