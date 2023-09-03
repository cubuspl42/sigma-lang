package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

object UndefinedType : PrimitiveType() {
    override fun findLowestCommonSupertype(
        other: Type,
    ): Type = when (other) {
        is UndefinedType -> UndefinedType
        else -> AnyType
    }

    override fun dump(): String = "Undefined"
}
