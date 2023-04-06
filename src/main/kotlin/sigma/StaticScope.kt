package sigma

import sigma.values.Symbol
import sigma.semantics.types.Type

interface StaticTypeScope {
    object Empty : StaticTypeScope {
        override fun getType(typeName: Symbol): Type? = null
    }

    fun getType(typeName: Symbol): Type?

    fun chainWith(
        backScope: StaticTypeScope,
    ): StaticTypeScope = object : StaticTypeScope {
        override fun getType(
            typeName: Symbol,
        ): Type? = this@StaticTypeScope.getType(
            typeName = typeName,
        ) ?: backScope.getType(
            typeName = typeName,
        )
    }
}

interface StaticValueScope {
    object Empty : StaticValueScope {
        override fun getValueType(
            valueName: Symbol,
        ): Type? = null
    }

    // Idea: Return `TypeExpression`?
    fun getValueType(valueName: Symbol): Type?

    fun chainWith(
        backScope: StaticValueScope,
    ): StaticValueScope = object : StaticValueScope {
        override fun getValueType(
            valueName: Symbol,
        ): Type? = this@StaticValueScope.getValueType(
            valueName = valueName,
        ) ?: backScope.getValueType(
            valueName = valueName,
        )
    }
}
