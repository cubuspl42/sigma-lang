package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface PostfixCallTerm : CallTerm {
    val subject: ExpressionTerm
    val argument: ExpressionTerm
}
