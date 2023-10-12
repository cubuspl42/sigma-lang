package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier

interface UnorderedTupleConstructorTerm : TupleConstructorTerm {
    val entries: List<Entry>

    interface Entry {
        val name: Identifier

        val value: ExpressionTerm
    }
}
