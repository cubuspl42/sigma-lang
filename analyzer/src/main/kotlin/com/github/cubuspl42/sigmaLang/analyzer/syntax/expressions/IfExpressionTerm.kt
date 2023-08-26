package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface IfExpressionTerm {
    val guard: ExpressionTerm
    val trueBranch: ExpressionTerm
    val falseBranch: ExpressionTerm
}
