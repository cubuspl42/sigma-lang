package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

sealed interface GenericConstructorTerm : ExpressionTerm {
    val metaArgumentType: TupleTypeConstructorTerm
    val body: ExpressionTerm
}
