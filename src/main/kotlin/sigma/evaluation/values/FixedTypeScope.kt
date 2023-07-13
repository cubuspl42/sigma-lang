package sigma.evaluation.values

import sigma.semantics.TypeScope
import sigma.semantics.types.Type
import sigma.semantics.types.TypeEntity

data class FixedTypeScope(
    private val entries: Map<Symbol, Type>,
) : TypeScope {
    override fun getTypeEntity(
        typeName: Symbol,
    ): TypeEntity? = entries[typeName]
}
