package sigma.evaluation.scope

import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.Symbol
import sigma.semantics.expressions.EvaluationContext
import sigma.semantics.expressions.Expression

class LoopedScope(
    private val outerScope: Scope,
    private val expressionByName: Map<Symbol, Expression>,
) : Scope {
    private val valueByName = mutableMapOf<Symbol, EvaluationResult?>()

    override fun getValue(
        context: EvaluationContext,
        name: Symbol,
    ): EvaluationResult? = valueByName.getOrPut(name) {
        expressionByName[name]?.evaluate(
            context = context,
            scope = this,
        )
    } ?: outerScope.getValue(
        context = context,
        name = name,
    )
}
