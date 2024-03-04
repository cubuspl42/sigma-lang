package com.github.cubuspl42.sigmaLang.utils

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
