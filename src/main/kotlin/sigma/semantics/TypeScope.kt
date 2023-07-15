package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.semantics.types.TypeEntity

interface TypeScope {
    object Empty : TypeScope {
        override fun getTypeDefinition(typeName: Symbol): TypeDefinition? = null
    }

    fun getTypeDefinition(typeName: Symbol): TypeDefinition?

    fun chainWith(
        backScope: TypeScope,
    ): TypeScope = object : TypeScope {
        override fun getTypeDefinition(
            typeName: Symbol,
        ): TypeDefinition? = this@TypeScope.getTypeDefinition(
            typeName = typeName,
        ) ?: backScope.getTypeDefinition(
            typeName = typeName,
        )
    }
}
