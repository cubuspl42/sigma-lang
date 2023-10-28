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

object MapUtils {

    /**
     * Merges two maps into one. If both maps contain the same key, the values
     * are combined using the given function.
     */
    fun <K, V> merge(
        map1: Map<K, V>,
        map2: Map<K, V>,
        combine: (V, V) -> V,
    ): Map<K, V> {
        val result = mutableMapOf<K, V>()

        for ((key, value) in map1) {
            result[key] = value
        }

        for ((key, value) in map2) {
            result[key] = result[key]?.let { combine(it, value) } ?: value
        }

        return result
    }
}
