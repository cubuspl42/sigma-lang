package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface CallTerm {
    val subject: ExpressionTerm
    val argument: ExpressionTerm
}
