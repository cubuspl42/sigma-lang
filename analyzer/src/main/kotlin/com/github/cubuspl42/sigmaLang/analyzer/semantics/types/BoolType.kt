package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

object BoolType : PrimitiveType() {
    override fun findLowestCommonSupertype(
        other: Type,
    ) = when (other) {
        is BoolType -> BoolType
        else -> AnyType
    }

    override fun dump(): String = "Bool"
}
