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
}
