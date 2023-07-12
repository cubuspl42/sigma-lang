package sigma.evaluation.scope

import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.semantics.expressions.Expression

class LoopedScope(
    private val context: Scope,
    private val expressionByName: Map<Symbol, Expression>,
) : Scope {
    private val valueByName = mutableMapOf<Symbol, Value?>()

    override fun getValue(
        name: Symbol,
    ): Value? = valueByName.getOrPut(name) {
        expressionByName[name]?.evaluate(
            scope = this,
        )
    } ?: context.getValue(
        name = name,
    )
}
