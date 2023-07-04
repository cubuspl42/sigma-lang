package sigma.evaluation.scope

import sigma.evaluation.Thunk
import sigma.evaluation.values.Symbol

class FixedScope(
    private val entries: Map<Symbol, Thunk>,
) : Scope {
    override fun getValue(name: Symbol): Thunk? = entries[name]
}
