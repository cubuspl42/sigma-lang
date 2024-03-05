package com.github.cubuspl42.sigmaLang.utils

fun <T, R> Iterable<T>.mapUniquely(
    transform: (T) -> R,
): Set<R> = map(transform).toSet()

data class Uncons<T>(
    val head: T,
    val tail: List<T>,
)

fun <T> Iterable<T>.uncons(): Uncons<T>? {
    val iterator = iterator()

    if (!iterator.hasNext()) {
        return null
    }

    return Uncons(
        head = iterator.next(),
        tail = iterator.asSequence().toList(),
    )
}
