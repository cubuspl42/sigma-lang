package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface AbstractionConstructorTerm : ExpressionTerm {
    val argumentType: TupleTypeConstructorTerm

    val declaredImageType: ExpressionTerm?

    val image: ExpressionTerm
}
