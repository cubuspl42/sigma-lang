package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface AbstractionTerm : ExpressionTerm {
    val genericParametersTuple: GenericParametersTuple?

    val argumentType: TupleTypeConstructorTerm

    val declaredImageType: ExpressionTerm?

    val image: ExpressionTerm
}
