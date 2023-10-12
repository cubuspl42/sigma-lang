package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier

interface OrderedTupleTypeConstructorTerm : TupleTypeConstructorTerm {
    interface Element {
        val name: Identifier?

        val type: ExpressionTerm
    }

    val elements: List<Element>
}
