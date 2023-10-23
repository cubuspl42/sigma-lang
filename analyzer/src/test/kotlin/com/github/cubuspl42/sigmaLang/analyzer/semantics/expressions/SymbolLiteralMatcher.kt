package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import utils.Matcher

class SymbolLiteralMatcher(
    private val value: Matcher<Symbol>,
) : Matcher<SymbolLiteral>() {
    override fun match(actual: SymbolLiteral) {
        value.match(actual = actual.value)
    }
}
