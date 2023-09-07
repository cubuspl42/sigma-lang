package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol

data class QualifiedPath(
    val segments: List<Symbol>,
) {
    companion object {
        val Root = QualifiedPath(
            segments = emptyList(),
        )
    }

    fun toSymbol(): Symbol = Symbol.of(
        name = segments.joinToString(separator = ".") { it.name },
    )

    fun extend(name: Symbol): QualifiedPath = QualifiedPath(
        segments = segments + name,
    )
}
