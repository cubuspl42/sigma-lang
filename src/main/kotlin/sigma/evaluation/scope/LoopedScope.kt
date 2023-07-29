package sigma.evaluation.scope

import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Thunk
import sigma.semantics.expressions.EvaluationContext
import sigma.semantics.expressions.Expression

class LoopedScope(
    private val outerScope: Scope,
    private val expressionByName: Map<Symbol, Expression>,
) : Scope {
    private val valueByName = mutableMapOf<Symbol, Thunk<*>?>()

    override fun getValue(
        name: Symbol,
    ): Thunk<*>? = valueByName.getOrPut(name) {
        expressionByName[name]?.bind(
            scope = this,
        )
    } ?: outerScope.getValue(
        name = name,
    )
}
