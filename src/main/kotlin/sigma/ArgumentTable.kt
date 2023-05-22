package sigma

import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.evaluation.scope.Scope

class ArgumentTable(
    private val name: Symbol,
    private val value: Value,
) : Scope {
    override fun get(
        name: Symbol,
    ): Value? = value.takeIf { name == this.name }
}
