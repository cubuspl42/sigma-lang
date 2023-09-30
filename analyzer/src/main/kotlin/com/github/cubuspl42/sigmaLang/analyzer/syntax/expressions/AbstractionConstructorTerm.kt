package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface AbstractionConstructorTerm : ExpressionTerm {
    val genericParametersTuple: GenericParametersTuple?

    val argumentType: TupleTypeConstructorTerm

    val declaredImageType: ExpressionTerm?

    val image: ExpressionTerm
}
