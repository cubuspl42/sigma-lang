package sigma.evaluation.scope

import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.Symbol

class ChainedScope(
    private val context: Scope,
    private val scope: Scope,
) : Scope {
    override fun getValue(
        name: Symbol,
    ): EvaluationResult? = scope.getValue(
        name = name,
    ) ?: context.getValue(
        name = name,
    )
}
