package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface MethodCallTerm : CallTerm {
    val self: ExpressionTerm
    val method: ReferenceTerm
    val argument: ExpressionTerm
}
