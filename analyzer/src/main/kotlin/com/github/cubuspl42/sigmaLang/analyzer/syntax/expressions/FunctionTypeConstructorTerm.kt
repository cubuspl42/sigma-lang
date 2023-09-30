package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface FunctionTypeConstructorTerm : ExpressionTerm {
    val metaArgumentType: TupleTypeConstructorTerm?
    val argumentType: TupleTypeConstructorTerm
    val imageType: ExpressionTerm
}
