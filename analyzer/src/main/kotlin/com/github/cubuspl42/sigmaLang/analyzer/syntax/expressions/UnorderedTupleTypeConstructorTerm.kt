package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol

interface UnorderedTupleTypeConstructorTerm : TupleTypeConstructorTerm {
    interface Entry {
        val name: Symbol

        val type: ExpressionTerm
    }


    val entries: List<Entry>
}
