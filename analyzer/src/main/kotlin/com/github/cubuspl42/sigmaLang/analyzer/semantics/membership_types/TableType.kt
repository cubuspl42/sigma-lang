package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

sealed class TableType : FunctionType() {
    final override val argumentType: MembershipType
        get() = keyType

    abstract override fun substituteTypeVariables(resolution: TypeVariableResolution): TableType

    final override val imageType: MembershipType
        get() = valueType // TODO: | undefined

    abstract val keyType: MembershipType

    /**
     * Any type but [UndefinedType]
     */
    abstract val valueType: MembershipType

    abstract fun isDefinitelyEmpty(): Boolean
}
