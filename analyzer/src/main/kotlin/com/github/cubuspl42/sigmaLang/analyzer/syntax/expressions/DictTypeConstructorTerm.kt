package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface DictTypeConstructorTerm : ExpressionTerm {
    val keyType: ExpressionTerm
    val valueType: ExpressionTerm
}
