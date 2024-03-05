package com.github.cubuspl42.sigmaLang.utils

import com.github.cubuspl42.sigmaLang.core.values.Value

object LazyUtils {
    fun lazier(block: () -> Lazy<Value>): Lazy<Value> = lazy { block().value }

    fun <T> looped(
        block: (lazy: Lazy<T>) -> T,
    ): T = object {
        val value: T by lazy { block(lazy { this.value }) }
    }.value

    fun <T1, T2> looped2(
        block: (
            lazy1: Lazy<T1>,
            lazy2: Lazy<T2>,
        ) -> Pair<T1, T2>,
    ): Pair<T1, T2> = object {
        val pairLazy: Pair<T1, T2> by lazy {
            block(
                lazy { pairLazy.first },
                lazy { pairLazy.second },
            )
        }
    }.pairLazy
}
