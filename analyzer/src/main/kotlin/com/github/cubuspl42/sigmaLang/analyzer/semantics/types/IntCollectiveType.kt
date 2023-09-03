package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

object IntCollectiveType : IntType() {
    override fun findLowestCommonSupertype(
        other: Type,
    ): Type = when (other) {
        is IntType -> IntCollectiveType
        else -> AnyType
    }

    override fun dump(): String = "Int"
}
