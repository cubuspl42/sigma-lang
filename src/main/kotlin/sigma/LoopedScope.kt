package sigma

import sigma.expressions.Expression

class LoopedScope(
    private val context: Table,
    private val declarations: Map<Symbol, Expression>,
) : Table() {
    private val environment: Table = this.chainWith(context)

    override fun read(argument: Value): Value? {
        if (argument !is Symbol) return null

        val value = declarations[argument] ?: return context.read(
            argument = argument,
        )

        return value.evaluate(context = environment)
    }

    override fun dumpContent(): String = "(looped scope)"
}
