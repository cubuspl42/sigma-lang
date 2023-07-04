package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.semantics.types.Type

interface TypeScope {
    object Empty : TypeScope {
        override fun getType(typeName: Symbol): Type? = null
    }

    fun getType(typeName: Symbol): Type?

    fun chainWith(
        backScope: TypeScope,
    ): TypeScope = object : TypeScope {
        override fun getType(
            typeName: Symbol,
        ): Type? = this@TypeScope.getType(
            typeName = typeName,
        ) ?: backScope.getType(
            typeName = typeName,
        )
    }
}