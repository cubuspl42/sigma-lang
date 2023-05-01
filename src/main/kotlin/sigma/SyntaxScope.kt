package sigma

import sigma.values.Symbol
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

interface SyntaxValueScope {
    object Empty : SyntaxValueScope {
        override fun getValueType(
            valueName: Symbol,
        ): Type? = null
    }

    // Idea: Return `TypeExpression`?
    fun getValueType(valueName: Symbol): Type?

    fun chainWith(
        backScope: SyntaxValueScope,
    ): SyntaxValueScope = object : SyntaxValueScope {
        override fun getValueType(
            valueName: Symbol,
        ): Type? = this@SyntaxValueScope.getValueType(
            valueName = valueName,
        ) ?: backScope.getValueType(
            valueName = valueName,
        )
    }
}
