package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.LocalDefinitionTerm

interface LetExpressionTerm {
    val definitions: List<LocalDefinitionTerm>

    val result: ExpressionTerm
}
