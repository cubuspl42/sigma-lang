package com.github.cubuspl42.sigmaLang.analyzer.utils

class UnorderedPair<T>(
    val one: T,
    val another: T,
) {
    override fun equals(other: Any?): Boolean {
        if (other !is UnorderedPair<*>) return false

        return (one == other.one && another == other.another) || (one == other.another && another == other.one)
    }

    override fun hashCode(): Int = one.hashCode() + another.hashCode()

    override fun toString(): String = "{$one, $another}"
}
