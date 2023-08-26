package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

interface DictConstructorTerm : ExpressionTerm {
    interface Association {
        val key: ExpressionTerm
        val value: ExpressionTerm
    }

    val associations: List<Association>
}
