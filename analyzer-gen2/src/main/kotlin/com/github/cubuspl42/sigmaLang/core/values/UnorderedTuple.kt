package com.github.cubuspl42.sigmaLang.core.values

class UnorderedTuple(
    private val valueByKey: Map<Identifier, Lazy<Value>>,
) : Callable() {
    companion object {
        val Empty = UnorderedTuple(valueByKey = emptyMap())
    }

    override fun call(argument: Value): Value = get(identifier = argument as Identifier)

    fun get(
        identifier: Identifier,
    ): Value = valueByKey[identifier]?.value ?: throw IllegalArgumentException("No such key: $identifier")
}
