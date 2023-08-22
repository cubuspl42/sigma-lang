package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.LocalDefinitionTerm

interface LetExpressionTerm : ExpressionTerm {
    val definitions: List<LocalDefinitionTerm>

    val result: ExpressionTerm
}
