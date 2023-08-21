package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol

interface OrderedTupleTypeConstructorTerm {
    interface Element {
        val name: Symbol?

        val type: ExpressionTerm
    }

    val elements: List<Element>
}
