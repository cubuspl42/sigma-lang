package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface GenericTypeConstructorTerm : ExpressionTerm {
    val genericParametersTuple: GenericParametersTuple
    val body: ExpressionTerm
}
