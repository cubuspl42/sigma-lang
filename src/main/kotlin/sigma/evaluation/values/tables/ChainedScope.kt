package sigma.evaluation.values.tables

import sigma.Thunk
import sigma.evaluation.values.Symbol

class ChainedScope(
    private val context: Scope,
    private val scope: Scope,
) : Scope {
    override fun get(
        name: Symbol,
    ): Thunk? = scope.get(
        name = name,
    ) ?: context.get(
        name = name,
    )
}
