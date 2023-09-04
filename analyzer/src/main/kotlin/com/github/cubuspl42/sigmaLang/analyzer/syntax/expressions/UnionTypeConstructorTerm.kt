package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface UnionTypeConstructorTerm : ExpressionTerm {
    val leftType: ExpressionTerm

    val rightType: ExpressionTerm
}
