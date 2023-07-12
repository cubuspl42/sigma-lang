package sigma.evaluation.scope

import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value

class ChainedScope(
    private val context: Scope,
    private val scope: Scope,
) : Scope {
    override fun getValue(
        name: Symbol,
    ): Value? = scope.getValue(
        name = name,
    ) ?: context.getValue(
        name = name,
    )
}
