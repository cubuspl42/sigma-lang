package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

interface Stub<out T> {
    companion object {
        fun <T> lazy(
            block: () -> Stub<T>,
        ): Stub<T> = object : Stub<T> {
            override val resolved: T by kotlin.lazy { block().resolved }
        }

        fun <T> of(
            value: T,
        ): Stub<T> = object : Stub<T> {
            override val resolved: T = value
        }
    }

    val resolved: T
}

fun <T> Stub<T>.asLazy(): Lazy<T> = object : Lazy<T> {
    override val value: T
        get() = this@asLazy.resolved

    override fun isInitialized(): Boolean = true
}

fun <T> Lazy<T>.asStub(): Stub<T> = object : Stub<T> {
    override val resolved: T by this@asStub
}
