package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.Wrapper
import com.github.cubuspl42.sigmaLang.core.values.Value

class LazyDynamicScope(
    private val dynamicScopeLazy: Lazy<DynamicScope>,
) : DynamicScope {

    private var isInitialized = false

    override fun getValue(referredWrapper: Wrapper): Value {
        if (!isInitialized) {
            throw IllegalStateException("DynamicScope is not initialized")
        }

        return dynamicScopeLazy.value.getValue(referredWrapper = referredWrapper)
    }

    fun init() {
        if (!isInitialized) {
            isInitialized = true
        }
    }
}
