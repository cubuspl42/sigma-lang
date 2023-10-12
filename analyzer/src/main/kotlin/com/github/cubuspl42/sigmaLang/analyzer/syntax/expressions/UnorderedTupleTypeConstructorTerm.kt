package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier

interface UnorderedTupleTypeConstructorTerm : TupleTypeConstructorTerm {
    interface Entry {
        val name: Identifier

        val type: ExpressionTerm
    }


    val entries: List<Entry>
}
