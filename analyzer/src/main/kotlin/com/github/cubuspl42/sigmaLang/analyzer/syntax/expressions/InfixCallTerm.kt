package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface InfixCallTerm : CallTerm {
    val operator: InfixOperator
    val leftArgument: ExpressionTerm
    val rightArgument: ExpressionTerm
}
