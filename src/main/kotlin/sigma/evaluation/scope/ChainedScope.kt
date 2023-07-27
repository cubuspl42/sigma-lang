package sigma.evaluation.scope

import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.Symbol
import sigma.semantics.expressions.EvaluationContext

class ChainedScope(
    private val outerScope: Scope,
    private val scope: Scope,
) : Scope {
    override fun getValue(
        context: EvaluationContext,
        name: Symbol,
    ): EvaluationResult? = scope.getValue(
        context = context,
        name = name,
    ) ?: outerScope.getValue(
        context = context,
        name = name,
    )
}
