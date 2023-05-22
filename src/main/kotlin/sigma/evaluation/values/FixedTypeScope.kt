package sigma.evaluation.values

import sigma.TypeScope
import sigma.semantics.types.Type

data class FixedTypeScope(
    private val entries: Map<Symbol, Type>,
) : TypeScope {
    override fun getType(
        typeName: Symbol,
    ): Type? = entries[typeName]
}
