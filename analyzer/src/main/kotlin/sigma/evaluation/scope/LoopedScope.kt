package sigma.evaluation.scope

import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.semantics.expressions.Expression

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
