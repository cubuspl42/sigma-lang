package sigma.values.tables

import sigma.Thunk
import sigma.values.Symbol

class FixedScope(
    private val entries: Map<Symbol, Thunk>,
) : Scope {
    override fun get(name: Symbol): Thunk? = entries[name]
}
