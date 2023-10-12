package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier

data class Formula(
    val name: Identifier,
) {
    companion object {
        fun of(s: String): Formula = Formula(
            name = Identifier.of(s)
        )
    }
}
