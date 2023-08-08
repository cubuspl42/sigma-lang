package sigma.evaluation.scope

import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.evaluation.values.asThunk

class FixedScope(
    private val entries: Map<Symbol, Value>,
) : Scope {
    override fun getValue(
        name: Symbol,
    ): Thunk<Value>? = entries[name]?.asThunk
}
