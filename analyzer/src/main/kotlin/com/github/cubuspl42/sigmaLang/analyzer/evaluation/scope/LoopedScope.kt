package com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression

class LoopedScope(
    private val outerScope: Scope,
    private val expressionByName: Map<Symbol, Expression>,
) : Scope {
    private val valueByName = mutableMapOf<Symbol, Thunk<Value>?>()

    override fun getValue(
        name: Symbol,
    ): Thunk<Value>? = valueByName.getOrPut(name) {
        expressionByName[name]?.let {
            Thunk.lazy {
                it.bind(scope = this@LoopedScope)
            }
        } ?: outerScope.getValue(
            name = name,
        )
    }
}
