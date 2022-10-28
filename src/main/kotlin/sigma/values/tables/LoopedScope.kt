package sigma.values.tables

import sigma.expressions.Expression
import sigma.values.Symbol
import sigma.values.Value



class LoopedScope(
    private val context: Table,
    private val declarations: Map<Symbol, Expression>,
) : Scope() {
    private val environment: Table = this.chainWith(context)

    override fun get(name: Symbol): Value? {
        val value = declarations[name] ?: return context.read(
            argument = name,
        )

        return value.evaluate(context = environment)
    }

    override fun dumpContent(): String = "(looped scope)"
}
