package sigma

import sigma.values.Symbol
import sigma.values.tables.Table
import sigma.values.Value
import sigma.values.tables.Scope

class ArgumentTable(
    private val name: Symbol,
    private val value: Value,
) : Scope {
    override fun get(
        name: Symbol,
    ): Value? = value.takeIf { name.isSame(this.name) }
}
