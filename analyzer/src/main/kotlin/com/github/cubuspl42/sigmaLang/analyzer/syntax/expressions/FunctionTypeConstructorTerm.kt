package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface FunctionTypeConstructorTerm : ExpressionTerm {
    val genericParametersTuple: GenericParametersTuple?
    val argumentType: TupleTypeConstructorTerm
    val imageType: ExpressionTerm
}
