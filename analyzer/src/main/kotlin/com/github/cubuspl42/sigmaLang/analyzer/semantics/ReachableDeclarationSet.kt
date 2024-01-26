package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.syntax.introductions.Declaration

data class ReachableDeclarationSet(
    val reachableDeclarations: Set<Declaration>,
) {
    companion object {
        fun unionAll(sets: Iterable<ReachableDeclarationSet>): ReachableDeclarationSet =
            ReachableDeclarationSet(
                reachableDeclarations = sets.flatMap { it.reachableDeclarations }.toSet(),
            )
    }
}
