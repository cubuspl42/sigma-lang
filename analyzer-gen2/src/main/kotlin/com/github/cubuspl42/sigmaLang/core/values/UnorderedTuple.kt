package com.github.cubuspl42.sigmaLang.core.values

class UnorderedTuple(
    override val valueByKey: Map<Identifier, Lazy<Value>>,
) : Indexable() {
    companion object {
        val Empty = UnorderedTuple(valueByKey = emptyMap())
    }

    fun unionWith(
        secondTuple: UnorderedTuple,
    ): UnorderedTuple = UnorderedTuple(
        valueByKey = valueByKey + secondTuple.valueByKey
    )

    fun extendWith(
        key: Identifier,
        valueLazy: Lazy<Value>,
    ): UnorderedTuple = UnorderedTuple(
        valueByKey = valueByKey + (key to valueLazy),
    )

    fun extendWith(
        key: Identifier,
        value: Value,
    ): UnorderedTuple = extendWith(
        key = key,
        valueLazy = lazyOf(value),
    )
}
