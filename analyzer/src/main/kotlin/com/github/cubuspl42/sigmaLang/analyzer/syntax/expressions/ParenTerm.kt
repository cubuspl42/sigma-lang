package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface ParenTerm : ExpressionTerm {
    val wrappedTerm: ExpressionTerm
}
