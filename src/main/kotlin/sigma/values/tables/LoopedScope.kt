package sigma.values.tables

import sigma.Thunk
import sigma.expressions.Expression
import sigma.values.Symbol

class LoopedScope(
    private val context: Scope,
    private val declarations: Map<Symbol, Expression>,
) : Scope() {
    override fun get(
        name: Symbol,
    ): Thunk? = declarations[name]?.evaluate(
        context = this,
    ) ?: context.get(
        name = name,
    )

    override fun dumpContent(): String = "(looped scope)"
}
