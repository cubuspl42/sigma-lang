package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

object UndefinedType : PrimitiveType() {
    override fun findLowestCommonSupertype(
        other: SpecificType,
    ): SpecificType = when (other) {
        is UndefinedType -> UndefinedType
        else -> AnyType
    }

    override fun dumpDirectly(depth: Int): String = "Undefined"
}
