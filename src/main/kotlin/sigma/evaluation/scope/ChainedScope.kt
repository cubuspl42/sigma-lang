package sigma.evaluation.scope

import sigma.Thunk
import sigma.evaluation.values.Symbol

class ChainedScope(
    private val context: Scope,
    private val scope: Scope,
) : Scope {
    override fun getValue(
        name: Symbol,
    ): Thunk? = scope.getValue(
        name = name,
    ) ?: context.getValue(
        name = name,
    )
}
