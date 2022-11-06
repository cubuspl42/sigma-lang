package sigma

import sigma.values.Symbol
import sigma.types.Type

interface StaticTypeScope {
    object Empty : StaticTypeScope {
        override fun getType(typeName: Symbol): Type? = null
    }

    fun getType(typeName: Symbol): Type?
}

interface StaticValueScope {
    object Empty : StaticValueScope {
        override fun getValueType(
            valueName: Symbol,
        ): Type? = null
    }

    fun getValueType(valueName: Symbol): Type?

    fun chainWith(
        valueScope: StaticValueScope,
    ): StaticValueScope = object : StaticValueScope {
        override fun getValueType(
            valueName: Symbol,
        ): Type? = this@StaticValueScope.getValueType(
            valueName = valueName,
        ) ?: valueScope.getValueType(
            valueName = valueName,
        )
    }
}
