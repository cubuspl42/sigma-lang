package sigma

import sigma.values.Symbol
import sigma.types.Type

interface StaticTypeScope {
    fun getType(typeName: Symbol): Type?
}

interface StaticValueScope {
    fun getValueType(valueName: Symbol): Type?
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
