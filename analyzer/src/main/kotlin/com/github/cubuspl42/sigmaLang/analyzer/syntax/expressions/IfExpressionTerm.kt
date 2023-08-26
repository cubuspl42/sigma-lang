package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface IfExpressionTerm : ExpressionTerm {
    val guard: ExpressionTerm
    val trueBranch: ExpressionTerm
    val falseBranch: ExpressionTerm
}
