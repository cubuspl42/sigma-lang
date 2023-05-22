package sigma.evaluation.scope

import sigma.Thunk
import sigma.syntax.expressions.ExpressionTerm
import sigma.evaluation.values.Symbol

class LoopedScope(
    private val context: Scope,
    private val declarations: Map<Symbol, ExpressionTerm>,
) : Scope {
    override fun getValue(
        name: Symbol,
    ): Thunk? = declarations[name]?.evaluate(
        scope = this,
    ) ?: context.getValue(
        name = name,
    )
}
