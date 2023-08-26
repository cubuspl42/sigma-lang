package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface SetConstructorTerm : ExpressionTerm {
    val elements: List<ExpressionTerm>
}
