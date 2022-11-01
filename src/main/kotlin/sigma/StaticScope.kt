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
    fun getValueType(valueName: Symbol): Type?

    fun asStaticScope(): StaticScope = StaticScope(
        typeScope = StaticTypeScope.Empty,
        valueScope = this,
    )

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

data class StaticScope(
    val typeScope: StaticTypeScope,
    val valueScope: StaticValueScope,
) : StaticTypeScope, StaticValueScope {
    override fun getType(
        typeName: Symbol,
    ): Type? = typeScope.getType(
        typeName = typeName,
    )

    override fun getValueType(
        valueName: Symbol,
    ): Type? = valueScope.getValueType(
        valueName = valueName,
    )
}
