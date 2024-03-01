package com.github.cubuspl42.sigmaLang.core.values

class UnorderedTuple(
    private val valueByKey: Map<Identifier, Lazy<Value>>,
) : Value(), Callable {
    companion object {
        val Empty = UnorderedTuple(valueByKey = emptyMap())
    }

    override fun call(argument: Value): Value =
        valueByKey[argument as Identifier]?.value ?: throw IllegalArgumentException("No such key")
}
