package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value

class UnorderedTupleConstructor(
    private val valueByKey: Map<Identifier, Lazy<Expression>>,
): Expression() {
    override fun bind(scope: DynamicScope): Lazy<Value> = lazyOf(
        UnorderedTuple(
            valueByKey = valueByKey.mapValues { (_, valueLazy) ->
                valueLazy.value.bind(scope = scope)
            }
        )
    )
}
