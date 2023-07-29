package sigma.evaluation.scope

import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.evaluation.values.ValueResult
import sigma.semantics.expressions.EvaluationContext

class FixedScope(
    private val entries: Map<Symbol, Value>,
) : Scope {
    override fun getValue(
        name: Symbol,
    ): Thunk<*>? = entries[name]?.asThunk
}
