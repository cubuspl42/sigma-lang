package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.*

sealed class SealedType : Type {
//    final override val asSealed: SealedType
//        get() = this


    final override fun toString(): String = dump()

    override val asLiteral: PrimitiveLiteralType? = null

    override val asArray: ArrayType? = null

    override fun walk(): Sequence<Type> = sequenceOf(this) + walkRecursive()
}

val <TypeType : Type> TypeType.asValue: TypeValue<TypeType>
    get() = TypeValue(this)