package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

object BoolType : PrimitiveType() {
    override fun findLowestCommonSupertype(
        other: MembershipType,
    ) = when (other) {
        is BoolType -> BoolType
        else -> AnyType
    }

    override fun dumpDirectly(depth: Int): String = "Bool"
}
