package sigma.evaluation.scope

import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.semantics.expressions.EvaluationContext

class FixedScope(
    private val entries: Map<Symbol, Value>,
) : Scope {
    override fun getValue(context: EvaluationContext, name: Symbol): EvaluationResult? = entries[name]
}
