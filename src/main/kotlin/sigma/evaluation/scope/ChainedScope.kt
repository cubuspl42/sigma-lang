package sigma.evaluation.scope

import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value

class ChainedScope(
    private val outerScope: Scope,
    private val scope: Scope,
) : Scope {
    override fun getValue(
        name: Symbol,
    ): Thunk<Value>? = scope.getValue(
        name = name,
    ) ?: outerScope.getValue(
        name = name,
    )
}
