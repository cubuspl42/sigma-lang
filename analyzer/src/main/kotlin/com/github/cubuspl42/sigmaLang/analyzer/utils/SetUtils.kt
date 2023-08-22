package com.github.cubuspl42.sigmaLang.analyzer.utils

object SetUtils {
    fun <E, R> unionAllOf(
        elements: Iterable<E>,
        extract: (E) -> Set<R>,
    ): Set<R> = elements.fold(
        initial = emptySet(),
    ) { acc, e ->
        acc + extract(e)
    }
}
