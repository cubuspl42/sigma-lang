package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.semantics.types.TypeEntity

interface TypeScope {
    object Empty : TypeScope {
        override fun getTypeEntity(typeName: Symbol): TypeEntity? = null
    }

    fun getTypeEntity(typeName: Symbol): TypeEntity?

    fun chainWith(
        backScope: TypeScope,
    ): TypeScope = object : TypeScope {
        override fun getTypeEntity(
            typeName: Symbol,
        ): TypeEntity? = this@TypeScope.getTypeEntity(
            typeName = typeName,
        ) ?: backScope.getTypeEntity(
            typeName = typeName,
        )
    }
}
