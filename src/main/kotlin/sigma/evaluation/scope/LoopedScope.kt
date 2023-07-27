package sigma.evaluation.scope

import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.Symbol
import sigma.semantics.expressions.Expression

class LoopedScope(
    private val context: Scope,
    private val expressionByName: Map<Symbol, Expression>,
) : Scope {
    private val valueByName = mutableMapOf<Symbol, EvaluationResult?>()

    override fun getValue(
        name: Symbol,
    ): EvaluationResult? = valueByName.getOrPut(name) {
        expressionByName[name]?.evaluate(
            scope = this,
        )
    } ?: context.getValue(
        name = name,
    )
}
