package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser

interface UnorderedTupleConstructorTerm {
    val entries: List<Entry>

    interface Entry {
        val name: Symbol

        val value: ExpressionTerm
    }
}
