package sigma.evaluation.values

import sigma.semantics.TypeDefinition
import sigma.semantics.TypeScope
import sigma.semantics.types.Type
import sigma.semantics.types.TypeEntity

data class FakeTypeScope(
    private val entries: Map<Symbol, Type>,
) : TypeScope {
    class FakeTypeDefinition(
        override val name: Symbol,
        override val definedType: Type,
    ) : TypeDefinition

    override fun getTypeDefinition(
        typeName: Symbol,
    ): TypeDefinition? = entries[typeName]?.let {
        FakeTypeDefinition(
            name = typeName,
            definedType = it,
        )
    }
}
