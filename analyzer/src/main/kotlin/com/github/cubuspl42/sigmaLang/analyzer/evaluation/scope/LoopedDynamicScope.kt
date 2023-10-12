package com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression

class LoopedDynamicScope(
    private val outerDynamicScope: DynamicScope,
    private val expressionByName: Map<Identifier, Expression>,
) : DynamicScope {
    private val valueByName = mutableMapOf<Identifier, Thunk<Value>?>()

    override fun getValue(
        name: Identifier,
    ): Thunk<Value>? = valueByName.getOrPut(name) {
        expressionByName[name]?.let {
            Thunk.lazy {
                it.bind(dynamicScope = this@LoopedDynamicScope)
            }
        } ?: outerDynamicScope.getValue(
            name = name,
        )
    }
}
