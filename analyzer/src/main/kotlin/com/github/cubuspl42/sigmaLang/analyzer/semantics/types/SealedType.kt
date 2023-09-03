package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.*

sealed class SealedType : SealedValue(), Type {
    final override val asSealed: SealedType
        get() = this

    final override val asValue: Value
        get() = this

    override val asValueThunk: Thunk<Type>
        get() = this.asThunk

    final override fun toString(): String = dump()

    override val asLiteral: PrimitiveLiteralType? = null

    override val asArray: ArrayType? = null

    override fun walk(): Sequence<Type> = sequenceOf(this) + walkRecursive()
}
