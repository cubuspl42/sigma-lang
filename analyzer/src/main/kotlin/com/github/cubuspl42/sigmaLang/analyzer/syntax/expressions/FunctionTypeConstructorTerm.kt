package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface FunctionTypeConstructorTerm {
    val genericParametersTuple: GenericParametersTuple?
    val argumentType: TupleTypeConstructorTerm
    val imageType: ExpressionTerm
}
