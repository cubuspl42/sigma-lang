package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

object StringType : PrimitiveType() {
    override fun findLowestCommonSupertype(
        other: Type,
    ) = when (other) {
        is StringType -> StringType
        else -> AnyType
    }

    override fun dump(): String = "String"
}
