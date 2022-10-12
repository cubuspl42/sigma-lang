package sigma

import java.lang.IllegalArgumentException

data class Table(
    val scope: Scope,
    val label: String? = null,
    val entries: Map<Symbol, Expression>,
) : FunctionValue() {
    companion object {
        val empty = Table(
            scope = Scope.Empty,
            entries = emptyMap(),
        )
    }

    override fun apply(
        argument: Value,
    ): Value {
        if (argument !is Symbol) throw IllegalArgumentException("Tables can only be called with symbols")

        val value = entries[argument] ?: throw IllegalStateException("Table @$label doesn't have key $argument")

        val extendedScope = when {
            label != null -> scope.extend(
                label = label,
                value = this,
            )

            else -> scope
        }

        return value.evaluate(scope = extendedScope)
    }

    override fun dump(): String = "(table)"
}
