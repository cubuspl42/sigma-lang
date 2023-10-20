package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

sealed class TableType : FunctionType() {
    final override val argumentType: TypeAlike
        get() = keyType

    abstract override fun substituteTypePlaceholders(resolution: TypePlaceholderResolution): TypePlaceholderSubstitution<TypeAlike>

    final override val imageType: TypeAlike
        get() = valueType // TODO: | undefined

    abstract val keyType: TypeAlike

    /**
     * Any type but [UndefinedType]
     */
    abstract val valueType: TypeAlike

    abstract fun isDefinitelyEmpty(): Boolean
}
