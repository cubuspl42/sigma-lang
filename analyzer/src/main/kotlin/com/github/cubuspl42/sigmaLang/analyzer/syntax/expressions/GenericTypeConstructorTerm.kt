package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface GenericTypeConstructorTerm {
    val genericParametersTuple: GenericParametersTuple
    val body: ExpressionTerm
}
