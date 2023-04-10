package sigma.values.tables

import sigma.Thunk
import sigma.syntax.expressions.ExpressionTerm
import sigma.values.Symbol

class LoopedScope(
    private val context: Scope,
    private val declarations: Map<Symbol, ExpressionTerm>,
) : Scope {
    override fun get(
        name: Symbol,
    ): Thunk? = declarations[name]?.evaluate(
        scope = this,
    ) ?: context.get(
        name = name,
    )
}
