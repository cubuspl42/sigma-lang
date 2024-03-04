package com.github.cubuspl42.sigmaLang.utils

import com.github.cubuspl42.sigmaLang.core.values.Value

object LazyUtils {
    fun lazier(block: () -> Lazy<Value>): Lazy<Value> = lazy { block().value }

    fun <T> looped(block: (lazy: Lazy<T>) -> T): T = object {
        val value1: T by lazy { block(lazy { this.value1 }) }
    }.value1
}
