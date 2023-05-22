package sigma.evaluation.values.tables

import sigma.Thunk
import sigma.evaluation.values.Symbol

class FixedScope(
    private val entries: Map<Symbol, Thunk>,
) : Scope {
    override fun get(name: Symbol): Thunk? = entries[name]
}
