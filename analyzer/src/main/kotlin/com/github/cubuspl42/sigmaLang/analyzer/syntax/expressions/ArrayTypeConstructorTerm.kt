package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface ArrayTypeConstructorTerm : ExpressionTerm {
    val elementType: ExpressionTerm
}
