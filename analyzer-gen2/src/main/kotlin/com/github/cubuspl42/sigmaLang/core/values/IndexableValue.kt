package com.github.cubuspl42.sigmaLang.core.values

abstract class IndexableValue : CallableValue() {
    final override fun call(argument: Value): Value = get(key = argument as Identifier)

    fun get(
        key: Identifier,
    ): Value = valueByKey[key]?.value ?: throw IllegalArgumentException("No such key: $key")

    abstract val valueByKey: Map<Identifier, Lazy<Value>>
}
