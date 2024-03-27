package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.Wrapper
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.utils.LazyUtils

data class DynamicContext(
    val rootLazy: Lazy<Value>,
    val scope: DynamicScope,
) {
    val root by rootLazy

    fun withScope(
        transform: DynamicScope.() -> DynamicScope,
    ): DynamicContext = copy(
        scope = scope.transform(),
    )
}

interface DynamicScope {
    data object Bottom : DynamicScope {
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
            val lazyScope = lazy(dynamicScopeLazy = dynamicScopeLooped)

            val pair = block(lazyScope)

            lazyScope.init()

            if (pair.second === lazyScope) {
                throw IllegalStateException("DynamicScope looped block must return a new DynamicScope")
            }

            pair
        }.first

        fun lazy(
            dynamicScopeLazy: Lazy<DynamicScope>,
        ): LazyDynamicScope = LazyDynamicScope(
            dynamicScopeLazy = dynamicScopeLazy,
        )
    }


    fun getValue(
        referredWrapper: Wrapper,
    ): Value
}

fun DynamicScope.withValue(
    wrapper: Wrapper,
    valueLazy: Lazy<Value>,
): DynamicScope = object : DynamicScope {
    override fun getValue(
        referredWrapper: Wrapper,
    ): Value = if (referredWrapper == wrapper) {
        valueLazy.value
    } else {
        this@withValue.getValue(referredWrapper = referredWrapper)
    }
}
