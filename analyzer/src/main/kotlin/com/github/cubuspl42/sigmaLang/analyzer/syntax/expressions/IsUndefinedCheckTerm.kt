package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface IsUndefinedCheckTerm : ExpressionTerm {
    val argument: ExpressionTerm
}
