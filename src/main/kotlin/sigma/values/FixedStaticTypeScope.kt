package sigma.values

import sigma.StaticTypeScope
import sigma.types.Type

data class FixedStaticTypeScope(
    private val entries: Map<Symbol, Type>,
) : StaticTypeScope {
    override fun getType(
        typeName: Symbol,
    ): Type? = entries[typeName]
}
