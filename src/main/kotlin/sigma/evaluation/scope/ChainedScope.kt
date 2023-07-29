package sigma.evaluation.scope

import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Thunk
import sigma.semantics.expressions.EvaluationContext

class ChainedScope(
    private val outerScope: Scope,
    private val scope: Scope,
) : Scope {
    override fun getValue(
        name: Symbol,
    ): Thunk<*>? = scope.getValue(
        name = name,
    ) ?: outerScope.getValue(
        name = name,
    )
}
