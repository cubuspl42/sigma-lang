package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

object StringType : PrimitiveType() {
    override fun findLowestCommonSupertype(
        other: SpecificType,
    ) = when (other) {
        is StringType -> StringType
        else -> AnyType
    }

    override fun dumpDirectly(depth: Int): String = "String"
}
