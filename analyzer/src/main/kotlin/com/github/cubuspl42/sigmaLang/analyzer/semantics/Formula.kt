package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol

data class Formula(
    val name: Symbol,
) {
    companion object {
        fun of(s: String): Formula = Formula(
            name = Identifier.of(s)
        )
    }
}
