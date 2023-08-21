package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

sealed class TableType : FunctionType() {
    final override val argumentType: Type
        get() = keyType

    final override val imageType: Type
        get() = valueType // TODO: | undefined

    abstract val keyType: Type

    /**
     * Any type but [UndefinedType]
     */
    abstract val valueType: Type

    abstract fun isDefinitelyEmpty(): Boolean
}
