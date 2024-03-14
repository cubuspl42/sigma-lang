package com.github.cubuspl42.sigmaLang.core.values

class UnorderedTupleValue(
    override val valueByKey: Map<Identifier, Lazy<Value>>,
) : IndexableValue() {
    companion object {
        val Empty = UnorderedTupleValue(valueByKey = emptyMap())
    }

    fun unionWith(
        secondTuple: UnorderedTupleValue,
    ): UnorderedTupleValue = UnorderedTupleValue(
        valueByKey = valueByKey + secondTuple.valueByKey
    )

    fun extendWith(
        key: Identifier,
        valueLazy: Lazy<Value>,
    ): UnorderedTupleValue = UnorderedTupleValue(
        valueByKey = valueByKey + (key to valueLazy),
    )

    fun extendWith(
        key: Identifier,
        value: Value,
    ): UnorderedTupleValue = extendWith(
        key = key,
        valueLazy = lazyOf(value),
    )
}
