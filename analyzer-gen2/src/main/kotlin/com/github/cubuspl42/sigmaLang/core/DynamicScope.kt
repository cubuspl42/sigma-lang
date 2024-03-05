package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.Wrapper
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.utils.LazyUtils

interface DynamicScope {
    object Bottom : DynamicScope {
        override fun getValue(referredWrapper: Wrapper): Value {
            throw IllegalStateException("Unresolved reference at runtime: $referredWrapper")
        }
    }

    companion object {
        fun <T> looped(
            block: (
                dynamicScopeLooped: DynamicScope,
            ) -> Pair<T, DynamicScope>,
        ): T = LazyUtils.looped2 { _, dynamicScopeLooped ->
            block(
                lazy(dynamicScopeLazy = dynamicScopeLooped),
            )
        }.first

        private fun lazy(
            dynamicScopeLazy: Lazy<DynamicScope>,
        ): DynamicScope = object : DynamicScope {
            override fun getValue(
                referredWrapper: Wrapper,
            ): Value = dynamicScopeLazy.value.getValue(
                referredWrapper = referredWrapper,
            )
        }
    }

    fun getValue(
        referredWrapper: Wrapper,
    ): Value
}

fun DynamicScope.withValue(
    wrapper: Wrapper,
    value: Value,
): DynamicScope = object : DynamicScope {
    override fun getValue(
        referredWrapper: Wrapper,
    ): Value = if (referredWrapper == wrapper) {
        value
    } else {
        this@withValue.getValue(referredWrapper = referredWrapper)
    }
}
