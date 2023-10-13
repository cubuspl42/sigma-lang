package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol

data class QualifiedPath(
    val segments: List<Identifier>,
): Symbol() {
    companion object {
        val Root = QualifiedPath(
            segments = emptyList(),
        )
    }

//    fun toSymbol(): Identifier = Identifier.of(
//        name = segments.joinToString(separator = ".") { it.name },
//    )

    fun extend(name: Identifier): QualifiedPath = QualifiedPath(
        segments = segments + name,
    )

    override fun dump(): String = segments.joinToString(separator = ".") { it.name }
}
