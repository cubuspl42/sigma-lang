package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

object StringType : PrimitiveType() {
    override fun findLowestCommonSupertype(
        other: MembershipType,
    ) = when (other) {
        is StringType -> StringType
        else -> AnyType
    }

    override fun dumpDirectly(depth: Int): String = "String"
}
