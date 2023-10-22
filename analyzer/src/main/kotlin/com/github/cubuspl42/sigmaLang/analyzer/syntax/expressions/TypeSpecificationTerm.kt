package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

sealed interface TypeSpecificationTerm : ExpressionTerm {
    val subject: ExpressionTerm
    val argument: TupleConstructorTerm
}
